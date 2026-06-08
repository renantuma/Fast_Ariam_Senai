package com.fastariam.service;

import com.fastariam.model.Cidade;
import com.fastariam.model.TarifaFrete;
import com.fastariam.repository.CidadeRepository;
import com.fastariam.repository.TarifaFreteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Service
public class RoteirizacaoService {

    private static final Logger log = Logger.getLogger(RoteirizacaoService.class.getName());
    private final WebClient.Builder webClientBuilder;
    private final CidadeRepository cidadeRepository;
    private final TarifaFreteRepository tarifaRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.googlemaps.api-key:}")
    private String googleMapsApiKey;

    private static final double PEDAGIO_KM_TRUCK = 0.08;
    private static final double PEDAGIO_KM_CARRETA = 0.12;
    private static final double FRETE_KM_TRUCK_PADRAO = 4.50;
    private static final double FRETE_KM_CARRETA_PADRAO = 5.80;

    public RoteirizacaoService(WebClient.Builder webClientBuilder,
                                CidadeRepository cidadeRepository,
                                TarifaFreteRepository tarifaRepository,
                                ObjectMapper objectMapper) {
        this.webClientBuilder = webClientBuilder;
        this.cidadeRepository = cidadeRepository;
        this.tarifaRepository = tarifaRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Consulta a distância da rota (Google Maps / OSRM / estimativa) e gera
     * uma tarifa de frete para a cidade de destino, persistindo cidade + tarifa.
     */
    public TarifaFrete gerarTarifaViaApi(String nomeCidade, String estado) {
        double distanciaKm;
        try {
            if (googleMapsApiKey != null && !googleMapsApiKey.isBlank() && !googleMapsApiKey.equals("SUA_CHAVE_AQUI")) {
                distanciaKm = buscarViaGoogleMaps(nomeCidade, estado);
            } else {
                distanciaKm = buscarViaOSRM(nomeCidade, estado);
            }
        } catch (Exception e) {
            log.warning("Erro ao consultar API de roteirização: " + e.getMessage());
            distanciaKm = estimarDistanciaPorEstado(estado);
        }

        // Reaproveita a cidade se já existir; caso contrário, cria a identidade.
        Cidade cidade = cidadeRepository.findByNomeAndEstado(nomeCidade, estado)
                .orElseGet(() -> cidadeRepository.save(
                        Cidade.builder().nome(nomeCidade).estado(estado.toUpperCase()).build()));

        TarifaFrete tarifa = TarifaFrete.builder()
                .cidade(cidade)
                .distanciaKm(distanciaKm)
                .pedagioTruck(distanciaKm * PEDAGIO_KM_TRUCK)
                .pedagioCarreta(distanciaKm * PEDAGIO_KM_CARRETA)
                .freteBaseKmTruck(FRETE_KM_TRUCK_PADRAO)
                .freteBaseKmCarreta(FRETE_KM_CARRETA_PADRAO)
                .origemApi(true)
                .build();

        return tarifaRepository.save(tarifa);
    }

    private double buscarViaOSRM(String nomeCidade, String estado) throws Exception {
        String destino = nomeCidade + ", " + estado + ", Brazil";
        String nominatimUrl = "https://nominatim.openstreetmap.org/search?q="
                + URLEncoder.encode(destino, StandardCharsets.UTF_8) + "&format=json&limit=1";
        WebClient client = webClientBuilder.defaultHeader("User-Agent", "FastAriamLogistica/1.0").build();
        String geocodeResp = client.get().uri(nominatimUrl).retrieve().bodyToMono(String.class).block();
        JsonNode geocodeJson = objectMapper.readTree(geocodeResp);
        if (geocodeJson.isEmpty()) throw new RuntimeException("Cidade não encontrada: " + nomeCidade);
        double latDest = geocodeJson.get(0).get("lat").asDouble();
        double lonDest = geocodeJson.get(0).get("lon").asDouble();
        double latOrigem = -23.3045, lonOrigem = -51.1696;
        String osrmUrl = String.format("http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false",
                lonOrigem, latOrigem, lonDest, latDest);
        String osrmResp = client.get().uri(osrmUrl).retrieve().bodyToMono(String.class).block();
        JsonNode osrmJson = objectMapper.readTree(osrmResp);
        if ("Ok".equals(osrmJson.get("code").asText())) {
            return osrmJson.get("routes").get(0).get("distance").asDouble() / 1000.0;
        }
        throw new RuntimeException("OSRM sem rota válida");
    }

    private double buscarViaGoogleMaps(String nomeCidade, String estado) throws Exception {
        String destino = URLEncoder.encode(nomeCidade + ", " + estado + ", Brasil", StandardCharsets.UTF_8);
        String origem = URLEncoder.encode("Londrina,PR,Brasil", StandardCharsets.UTF_8);
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origem
                + "&destinations=" + destino + "&key=" + googleMapsApiKey + "&language=pt-BR";
        String resp = webClientBuilder.build().get().uri(url).retrieve().bodyToMono(String.class).block();
        JsonNode json = objectMapper.readTree(resp);
        JsonNode element = json.get("rows").get(0).get("elements").get(0);
        if ("OK".equals(element.get("status").asText())) {
            return element.get("distance").get("value").asDouble() / 1000.0;
        }
        throw new RuntimeException("Google Maps sem resultado");
    }

    private double estimarDistanciaPorEstado(String estado) {
        return switch (estado.toUpperCase()) {
            case "SP" -> 430.0; case "MG" -> 700.0; case "RJ" -> 900.0;
            case "SC" -> 500.0; case "RS" -> 900.0; case "MS" -> 600.0;
            case "MT" -> 1200.0; case "GO" -> 900.0; case "DF" -> 1000.0;
            case "BA" -> 1800.0; case "PE" -> 2500.0; case "CE" -> 2800.0;
            case "AM" -> 3500.0; case "PA" -> 3000.0;
            default -> 1500.0;
        };
    }
}
