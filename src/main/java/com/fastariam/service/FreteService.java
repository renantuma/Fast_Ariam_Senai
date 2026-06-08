package com.fastariam.service;

import com.fastariam.dto.ResultadoFreteDTO;
import com.fastariam.model.*;
import com.fastariam.repository.*;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class FreteService {

    private static final Logger log = Logger.getLogger(FreteService.class.getName());
    private final TarifaFreteRepository tarifaRepo;
    private final ConfiguracaoFreteRepository configRepo;
    private final RoteirizacaoService roteirizacaoService;

    public FreteService(TarifaFreteRepository tarifaRepo,
                        ConfiguracaoFreteRepository configRepo,
                        RoteirizacaoService roteirizacaoService) {
        this.tarifaRepo = tarifaRepo;
        this.configRepo = configRepo;
        this.roteirizacaoService = roteirizacaoService;
    }

    private static final Map<String, Double> ICMS_ESTADO = Map.ofEntries(
        Map.entry("AC", 7.0), Map.entry("AL", 7.0), Map.entry("AM", 7.0),
        Map.entry("AP", 7.0), Map.entry("BA", 7.0), Map.entry("CE", 7.0),
        Map.entry("DF", 7.0), Map.entry("ES", 7.0), Map.entry("GO", 7.0),
        Map.entry("MA", 7.0), Map.entry("MS", 7.0), Map.entry("MT", 7.0),
        Map.entry("PA", 7.0), Map.entry("PB", 7.0), Map.entry("PE", 7.0),
        Map.entry("PI", 7.0), Map.entry("RN", 7.0), Map.entry("RO", 7.0),
        Map.entry("RR", 7.0), Map.entry("SE", 7.0), Map.entry("TO", 7.0),
        Map.entry("MG", 12.0), Map.entry("RJ", 12.0), Map.entry("RS", 12.0),
        Map.entry("SC", 12.0), Map.entry("SP", 12.0),
        Map.entry("PR", 18.0)
    );

    public ResultadoFreteDTO calcular(String nomeCidade, String estado,
                                       double valorMercadoria, double fatorDescargaCliente,
                                       double volumeTotal) {
        // Busca a tarifa da rota; se não existir, gera via API de roteirização.
        TarifaFrete tarifa = tarifaRepo.buscarPorCidade(nomeCidade, estado).orElse(null);
        boolean viaApi = false;
        if (tarifa == null) {
            log.info("Tarifa para " + nomeCidade + "/" + estado + " não cadastrada, consultando API...");
            tarifa = roteirizacaoService.gerarTarifaViaApi(nomeCidade, estado);
            viaApi = true;
        }

        ConfiguracaoFrete config = configRepo.getConfiguracao();
        if (config == null) throw new IllegalStateException("Configuração de frete não encontrada.");

        // ICMS é derivado do estado (regra fiscal), não armazenado por cidade.
        double icms = getIcmsEstado(estado);
        double pisCofins = config.getPisCofinsPercent();
        double margemComercial = config.getMargemComercialPercent();

        // Frete-base agora é responsabilidade da própria tarifa (modelo rico).
        double freteBaseTruck = tarifa.freteBaseTruck();
        double freteBaseCarreta = tarifa.freteBaseCarreta();

        double adValorem = valorMercadoria * (config.getAdValoremDefault() / 100.0);
        double descargarTruck = config.getDescargarManualTruck() * fatorDescargaCliente;
        double descargarCarreta = config.getDescargarManualCarreta() * fatorDescargaCliente;
        double pctImpostos = (icms + pisCofins) / 100.0;

        double subtotalTruck = freteBaseTruck + tarifa.getPedagioTruck() + descargarTruck + adValorem;
        double impostosTruck = subtotalTruck * pctImpostos;
        double comImpostosTruck = subtotalTruck + impostosTruck;
        double margemTruck = comImpostosTruck * (margemComercial / 100.0);
        double totalTruck = comImpostosTruck + margemTruck;

        double subtotalCarreta = freteBaseCarreta + tarifa.getPedagioCarreta() + descargarCarreta + adValorem;
        double impostosCarreta = subtotalCarreta * pctImpostos;
        double comImpostosCarreta = subtotalCarreta + impostosCarreta;
        double margemCarreta = comImpostosCarreta * (margemComercial / 100.0);
        double totalCarreta = comImpostosCarreta + margemCarreta;

        return ResultadoFreteDTO.builder()
                .cidadeDestino(nomeCidade).estadoDestino(estado)
                .distanciaKm(tarifa.getDistanciaKm())
                .freteBaseTruck(r2(freteBaseTruck)).pedagioTruck(r2(tarifa.getPedagioTruck()))
                .descargarTruck(r2(descargarTruck)).adValoremTruck(r2(adValorem))
                .impostosTruck(r2(impostosTruck)).subtotalTruck(r2(subtotalTruck))
                .margemTruck(r2(margemTruck)).totalTruck(r2(totalTruck))
                .freteBaseCarreta(r2(freteBaseCarreta)).pedagioCarreta(r2(tarifa.getPedagioCarreta()))
                .descargarCarreta(r2(descargarCarreta)).adValoremCarreta(r2(adValorem))
                .impostosCarreta(r2(impostosCarreta)).subtotalCarreta(r2(subtotalCarreta))
                .margemCarreta(r2(margemCarreta)).totalCarreta(r2(totalCarreta))
                .icmsPercent(icms).pisCofinsPercent(pisCofins)
                .margemComercialPercent(margemComercial).fatorDescargaCliente(fatorDescargaCliente)
                .veiculoRecomendado(TipoVeiculo.sugerirPorVolume(volumeTotal))
                .cidadeViaApi(viaApi || tarifa.isOrigemApi()).build();
    }

    /** Alíquota de ICMS conforme o estado de destino (regra fiscal centralizada). */
    public static double getIcmsEstado(String estado) {
        return ICMS_ESTADO.getOrDefault(estado.toUpperCase(), 7.0);
    }

    private double r2(double v) { return Math.round(v * 100.0) / 100.0; }
}
