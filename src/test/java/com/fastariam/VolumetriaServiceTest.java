package com.fastariam;

import com.fastariam.dto.ResultadoVolumetriaDTO;
import com.fastariam.model.*;
import com.fastariam.repository.ConfiguracaoFreteRepository;
import com.fastariam.service.VolumetriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolumetriaServiceTest {

    @Mock
    private ConfiguracaoFreteRepository configRepo;
    private VolumetriaService service;

    @BeforeEach
    void setup() {
        ConfiguracaoFrete config = ConfiguracaoFrete.builder()
                .capacidadeCarretaM3(60.0).fatorMetrosCarroceria(12.0).build();
        when(configRepo.getConfiguracao()).thenReturn(config);
        service = new VolumetriaService(configRepo);
    }

    @Test
    void calcularVolumeRefrigerado() {
        Produto p = Produto.builder().codigo("FCA-4-100").descricao("Expositor")
                .tipo(TipoProduto.REFRIGERADO).comprimento(1.56).largura(1.20).altura(1.72).build();
        ItemPedido item = ItemPedido.builder().produto(p).quantidade(2).build();
        ResultadoVolumetriaDTO result = service.calcular(List.of(item));
        assertThat(result.getVolumeTotalM3()).isCloseTo(6.44, offset(0.1));
    }

    @Test
    void sugestaoVeiculoPorVolume() {
        assertThat(TipoVeiculo.sugerirPorVolume(5.0)).isEqualTo(TipoVeiculo.VUC);
        assertThat(TipoVeiculo.sugerirPorVolume(35.0)).isEqualTo(TipoVeiculo.TRUCK);
        assertThat(TipoVeiculo.sugerirPorVolume(65.0)).isEqualTo(TipoVeiculo.CARRETA);
    }
}
