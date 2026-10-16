package com.fastariam;

import com.fastariam.dto.ResultadoFreteDTO;
import com.fastariam.model.Cidade;
import com.fastariam.model.ConfiguracaoFrete;
import com.fastariam.model.TarifaFrete;
import com.fastariam.model.TipoVeiculo;
import com.fastariam.repository.ConfiguracaoFreteRepository;
import com.fastariam.repository.TarifaFreteRepository;
import com.fastariam.service.FreteService;
import com.fastariam.service.RoteirizacaoService;
import com.fastariam.util.Dinheiro;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreteServiceTest {

    @Mock private TarifaFreteRepository tarifaRepo;
    @Mock private ConfiguracaoFreteRepository configRepo;
    @Mock private RoteirizacaoService roteirizacaoService;

    @Test
    void icmsPorEstado() {
        assertThat(FreteService.getIcmsEstado("PR")).isEqualTo(18.0);
        assertThat(FreteService.getIcmsEstado("SP")).isEqualTo(12.0);
        assertThat(FreteService.getIcmsEstado("BA")).isEqualTo(7.0);
        assertThat(FreteService.getIcmsEstado("pr")).isEqualTo(18.0);
        assertThat(FreteService.getIcmsEstado("XX")).isEqualTo(7.0);
    }

    /**
     * Valores esperados calculados de forma independente (Decimal em Python),
     * com a mesma fórmula do sistema original.
     */
    @Test
    void calculaFreteTruckECarretaComBigDecimal() {
        Cidade sp = Cidade.builder().nome("São Paulo").estado("SP").build();
        TarifaFrete tarifa = TarifaFrete.builder().cidade(sp).distanciaKm(430)
                .valorPorKmTruck(Dinheiro.reais(4.80)).valorPorKmCarreta(Dinheiro.reais(6.20))
                .pedagioTruck(Dinheiro.reais(180)).pedagioCarreta(Dinheiro.reais(280)).build();
        ConfiguracaoFrete config = ConfiguracaoFrete.builder()
                .custoDescargaManualTruck(Dinheiro.reais(280)).custoDescargaManualCarreta(Dinheiro.reais(420))
                .pisCofinsPercent(Dinheiro.reais(9.25)).margemComercialPercent(Dinheiro.reais(20))
                .adValoremPercent(Dinheiro.reais(0.30)).build();

        when(tarifaRepo.buscarPorCidade("São Paulo", "SP")).thenReturn(Optional.of(tarifa));
        when(configRepo.getConfiguracao()).thenReturn(config);

        FreteService service = new FreteService(tarifaRepo, configRepo, roteirizacaoService);
        ResultadoFreteDTO r = service.calcular("São Paulo", "SP", new BigDecimal("100000"), 1.5, 35.0);

        assertThat(r.getTruck().getFreteBase()).isEqualByComparingTo("2064.00");
        assertThat(r.getTruck().getPedagio()).isEqualByComparingTo("180.00");
        assertThat(r.getTruck().getCustoDescarga()).isEqualByComparingTo("420.00");
        assertThat(r.getTruck().getAdValorem()).isEqualByComparingTo("300.00");
        assertThat(r.getTruck().getSubtotal()).isEqualByComparingTo("2964.00");
        assertThat(r.getTruck().getImpostos()).isEqualByComparingTo("629.85");
        assertThat(r.getTruck().getMargemComercialValor()).isEqualByComparingTo("718.77");
        assertThat(r.getTruck().getTotal()).isEqualByComparingTo("4312.62");

        assertThat(r.getCarreta().getFreteBase()).isEqualByComparingTo("2666.00");
        assertThat(r.getCarreta().getCustoDescarga()).isEqualByComparingTo("630.00");
        assertThat(r.getCarreta().getSubtotal()).isEqualByComparingTo("3876.00");
        assertThat(r.getCarreta().getImpostos()).isEqualByComparingTo("823.65");
        assertThat(r.getCarreta().getMargemComercialValor()).isEqualByComparingTo("939.93");
        assertThat(r.getCarreta().getTotal()).isEqualByComparingTo("5639.58");

        assertThat(r.getValorMercadoria()).isEqualByComparingTo("100000.00");
        assertThat(r.getVeiculoRecomendado()).isEqualTo(TipoVeiculo.TRUCK);
        assertThat(r.isTarifaGeradaViaApi()).isFalse();
    }

    @Test
    void opcaoParaUsaPrecoDeCarretaSomenteParaCarreta() {
        Cidade c = Cidade.builder().nome("X").estado("PR").build();
        TarifaFrete tarifa = TarifaFrete.builder().cidade(c).distanciaKm(100)
                .valorPorKmTruck(Dinheiro.reais(4)).valorPorKmCarreta(Dinheiro.reais(5)).build();
        ConfiguracaoFrete config = ConfiguracaoFrete.builder().build();
        when(tarifaRepo.buscarPorCidade("X", "PR")).thenReturn(Optional.of(tarifa));
        when(configRepo.getConfiguracao()).thenReturn(config);

        ResultadoFreteDTO r = new FreteService(tarifaRepo, configRepo, roteirizacaoService)
                .calcular("X", "PR", BigDecimal.ZERO, 1.0, 10.0);

        assertThat(r.opcaoPara(TipoVeiculo.CARRETA)).isSameAs(r.getCarreta());
        assertThat(r.opcaoPara(TipoVeiculo.TRUCK)).isSameAs(r.getTruck());
        assertThat(r.opcaoPara(TipoVeiculo.BITREM)).isSameAs(r.getTruck());
    }
}
