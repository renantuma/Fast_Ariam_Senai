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
    private final ConfiguracaoFreteRepository configuracaoRepo;

    public VolumetriaService(ConfiguracaoFreteRepository configuracaoRepo) {
        this.configuracaoRepo = configuracaoRepo;
    }

    public ResultadoVolumetriaDTO calcular(List<ItemPedido> itens) {
        ConfiguracaoFrete config = configuracaoRepo.getConfiguracao();
        double capacidadeRef = (config != null) ? config.getCapacidadeCarretaM3() : 60.0;
        double fatorMetros = (config != null) ? config.getFatorMetrosCarroceria() : 12.0;

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
                double volItem = item.getQuantidade() / p.getFatorQtdM3();
                somaCategoria += volItem;
                itensSeca.add(ResultadoVolumetriaDTO.ItemVolumeDTO.builder()
                        .codigo(p.getCodigo()).descricao(p.getDescricao())
                        .tipo(TipoProduto.LINHA_SECA).quantidade(item.getQuantidade())
                        .volumeUnitario(1.0 / p.getFatorQtdM3()).volumeTotal(volItem)
                        .fatorAjuste(cat.getFatorAjuste()).volumeAjustado(volItem * cat.getFatorAjuste())
                        .categoria(cat.getDescricao()).build());
            }
            double volumeAjustadoCategoria = somaCategoria * cat.getFatorAjuste();
            volumePorCategoria.put(cat.getDescricao(), volumeAjustadoCategoria);
            volumeLinhaSeca += volumeAjustadoCategoria;
        }

        double volumeTotal = volumeLinhaSeca + volumeRefrigerado;
        double metros = volumeTotal * fatorMetros / capacidadeRef;

        log.info("Volumetria calculada: " + volumeTotal + " m3");

        return ResultadoVolumetriaDTO.builder()
                .volumeTotalM3(r2(volumeTotal)).metrosCarroceria(r2(metros))
                .metrosComMargemNvia(r2(metros * 1.10)).metrosComMargemVenda(r2(metros * 1.20))
                .veiculoSugerido(TipoVeiculo.sugerirPorVolume(volumeTotal))
                .itensSeca(itensSeca).itensRefrigerado(itensRefrig)
                .volumePorCategoria(volumePorCategoria).build();
    }

    private double r2(double v) { return Math.round(v * 100.0) / 100.0; }
}
