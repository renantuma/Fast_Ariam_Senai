package com.fastariam.service;

import com.fastariam.dto.ResultadoVolumetriaDTO;
import com.fastariam.model.*;
import com.fastariam.repository.ConfiguracaoFreteRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class VolumetriaService {

    private static final Logger log = Logger.getLogger(VolumetriaService.class.getName());

    /** Margem de segurança NVIA aplicada sobre o comprimento ocupado (+10%). */
    private static final double MARGEM_NVIA = 1.10;
    /** Margem de venda aplicada sobre o comprimento ocupado (+20%). */
    private static final double MARGEM_VENDA = 1.20;
    private final ConfiguracaoFreteRepository configuracaoRepo;

    public VolumetriaService(ConfiguracaoFreteRepository configuracaoRepo) {
        this.configuracaoRepo = configuracaoRepo;
    }

    public ResultadoVolumetriaDTO calcular(List<ItemPedido> itens) {
        ConfiguracaoFrete config = configuracaoRepo.getConfiguracao();
        double volumeReferencia = (config != null) ? config.getVolumeUtilCarretaM3() : 60.0;
        double comprimentoReferencia = (config != null) ? config.getComprimentoCarroceriaCarretaM() : 12.0;

        List<ResultadoVolumetriaDTO.ItemVolumeDTO> itensSeca = new ArrayList<>();
        List<ResultadoVolumetriaDTO.ItemVolumeDTO> itensRefrig = new ArrayList<>();

        double volumeRefrigerado = 0.0;
        for (ItemPedido item : itens) {
            if (item.getProduto() == null || item.getProduto().getTipo() != TipoProduto.REFRIGERADO) continue;
            Produto p = item.getProduto();
            double volUnit = p.getVolumeUnitario();
            double volTotal = volUnit * item.getQuantidade();
            volumeRefrigerado += volTotal;
            itensRefrig.add(ResultadoVolumetriaDTO.ItemVolumeDTO.builder()
                    .codigo(p.getCodigo()).descricao(p.getDescricao())
                    .tipo(TipoProduto.REFRIGERADO).quantidade(item.getQuantidade())
                    .volumeUnitario(volUnit).volumeTotal(volTotal)
                    .fatorAjuste(1.0).volumeAjustado(volTotal).categoria("Refrigerado").build());
        }

        Map<CategoriaLinhaSeca, List<ItemPedido>> porCategoria = itens.stream()
                .filter(i -> i.getProduto() != null && i.getProduto().getTipo() == TipoProduto.LINHA_SECA)
                .collect(Collectors.groupingBy(i -> i.getProduto().getCategoria()));

        Map<String, Double> volumePorCategoria = new LinkedHashMap<>();
        double volumeLinhaSeca = 0.0;

        for (Map.Entry<CategoriaLinhaSeca, List<ItemPedido>> entry : porCategoria.entrySet()) {
            CategoriaLinhaSeca cat = entry.getKey();
            double somaCategoria = 0.0;
            for (ItemPedido item : entry.getValue()) {
                Produto p = item.getProduto();
                double volItem = item.getQuantidade() / p.getPecasPorM3();
                somaCategoria += volItem;
                itensSeca.add(ResultadoVolumetriaDTO.ItemVolumeDTO.builder()
                        .codigo(p.getCodigo()).descricao(p.getDescricao())
                        .tipo(TipoProduto.LINHA_SECA).quantidade(item.getQuantidade())
                        .volumeUnitario(1.0 / p.getPecasPorM3()).volumeTotal(volItem)
                        .fatorAjuste(cat.getFatorAjuste()).volumeAjustado(volItem * cat.getFatorAjuste())
                        .categoria(cat.getDescricao()).build());
            }
            double volumeAjustadoCategoria = somaCategoria * cat.getFatorAjuste();
            volumePorCategoria.put(cat.getDescricao(), volumeAjustadoCategoria);
            volumeLinhaSeca += volumeAjustadoCategoria;
        }

        double volumeTotal = volumeLinhaSeca + volumeRefrigerado;
        // m³ → metros lineares de carroceria, proporcional à carreta de referência.
        double comprimentoOcupado = volumeTotal * comprimentoReferencia / volumeReferencia;

        log.info("Volumetria calculada: " + volumeTotal + " m3");

        return ResultadoVolumetriaDTO.builder()
                .volumeTotalM3(r2(volumeTotal)).comprimentoOcupadoM(r2(comprimentoOcupado))
                .comprimentoMargemNviaM(r2(comprimentoOcupado * MARGEM_NVIA))
                .comprimentoMargemVendaM(r2(comprimentoOcupado * MARGEM_VENDA))
                .veiculoSugerido(TipoVeiculo.sugerirPorVolume(volumeTotal))
                .itensSeca(itensSeca).itensRefrigerado(itensRefrig)
                .volumePorCategoria(volumePorCategoria).build();
    }

    private double r2(double v) { return Math.round(v * 100.0) / 100.0; }
}
