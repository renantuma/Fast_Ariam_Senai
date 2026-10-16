package com.fastariam.service;

import com.fastariam.dto.OpcaoFreteDTO;
import com.fastariam.dto.ResultadoFreteDTO;
import com.fastariam.model.*;
import com.fastariam.repository.*;
import com.fastariam.util.Dinheiro;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
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

    /**
     * Calcula o frete (Truck e Carreta) para a rota, mercadoria e cliente informados.
     *
     * <p>Fórmula, para cada veículo:
     * {@code subtotal = freteBase + pedágio + descarga + adValorem};
     * {@code impostos = subtotal × (ICMS + PIS/COFINS)};
     * {@code margem = (subtotal + impostos) × margemComercial};
     * {@code total = subtotal + impostos + margem}.
     * As contas usam {@link BigDecimal} com precisão alta e só arredondam
     * (para centavos) ao montar o resultado.</p>
     */
    public ResultadoFreteDTO calcular(String nomeCidade, String estado,
                                       BigDecimal valorMercadoria, double fatorDescargaCliente,
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
        BigDecimal icmsPercent = BigDecimal.valueOf(getIcmsEstado(estado));
        BigDecimal pisCofinsPercent = config.getPisCofinsPercent();
        BigDecimal margemPercent = config.getMargemComercialPercent();

        BigDecimal fatorImpostos = Dinheiro.fator(icmsPercent.add(pisCofinsPercent));
        BigDecimal fatorMargem = Dinheiro.fator(margemPercent);
        BigDecimal adValorem = valorMercadoria.multiply(Dinheiro.fator(config.getAdValoremPercent()));
        BigDecimal fatorDescarga = BigDecimal.valueOf(fatorDescargaCliente);

        OpcaoFreteDTO truck = calcularOpcao(
                tarifa.freteBaseTruck(), tarifa.getPedagioTruck(),
                config.getCustoDescargaManualTruck().multiply(fatorDescarga),
                adValorem, fatorImpostos, fatorMargem);

        OpcaoFreteDTO carreta = calcularOpcao(
                tarifa.freteBaseCarreta(), tarifa.getPedagioCarreta(),
                config.getCustoDescargaManualCarreta().multiply(fatorDescarga),
                adValorem, fatorImpostos, fatorMargem);

        return ResultadoFreteDTO.builder()
                .cidadeDestino(nomeCidade).estadoDestino(estado)
                .distanciaKm(tarifa.getDistanciaKm())
                .valorMercadoria(Dinheiro.arredondar(valorMercadoria))
                .truck(truck).carreta(carreta)
                .icmsPercent(icmsPercent).pisCofinsPercent(pisCofinsPercent)
                .margemComercialPercent(margemPercent).adValoremPercent(config.getAdValoremPercent())
                .fatorDescargaCliente(fatorDescargaCliente)
                .veiculoRecomendado(TipoVeiculo.sugerirPorVolume(volumeTotal))
                .tarifaGeradaViaApi(viaApi || tarifa.isGeradaViaApi())
                .build();
    }

    /** Monta o resultado de um veículo. Recebe valores sem arredondar e devolve em centavos. */
    private OpcaoFreteDTO calcularOpcao(BigDecimal freteBase, BigDecimal pedagio, BigDecimal custoDescarga,
                                         BigDecimal adValorem, BigDecimal fatorImpostos, BigDecimal fatorMargem) {
        BigDecimal subtotal = freteBase.add(pedagio).add(custoDescarga).add(adValorem);
        BigDecimal impostos = subtotal.multiply(fatorImpostos);
        BigDecimal comImpostos = subtotal.add(impostos);
        BigDecimal margem = comImpostos.multiply(fatorMargem);
        BigDecimal total = comImpostos.add(margem);

        return new OpcaoFreteDTO(
                Dinheiro.arredondar(freteBase), Dinheiro.arredondar(pedagio),
                Dinheiro.arredondar(custoDescarga), Dinheiro.arredondar(adValorem),
                Dinheiro.arredondar(subtotal), Dinheiro.arredondar(impostos),
                Dinheiro.arredondar(margem), Dinheiro.arredondar(total));
    }

    /** Alíquota de ICMS conforme o estado de destino (regra fiscal centralizada). */
    public static double getIcmsEstado(String estado) {
        return ICMS_ESTADO.getOrDefault(estado.toUpperCase(), 7.0);
    }

}
