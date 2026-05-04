package com.fastariam.dto;

import com.fastariam.model.TipoProduto;
import com.fastariam.model.TipoVeiculo;
import java.util.List;
import java.util.Map;

public class ResultadoVolumetriaDTO {

    private double volumeTotalM3;
    private double metrosCarroceria;
    private double metrosComMargemNvia;
    private double metrosComMargemVenda;
    private TipoVeiculo veiculoSugerido;
    private List<ItemVolumeDTO> itensSeca;
    private List<ItemVolumeDTO> itensRefrigerado;
    private Map<String, Double> volumePorCategoria;

    public ResultadoVolumetriaDTO() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private double volumeTotalM3, metrosCarroceria, metrosComMargemNvia, metrosComMargemVenda;
        private TipoVeiculo veiculoSugerido;
        private List<ItemVolumeDTO> itensSeca, itensRefrigerado;
        private Map<String, Double> volumePorCategoria;

        public Builder volumeTotalM3(double v) { this.volumeTotalM3 = v; return this; }
        public Builder metrosCarroceria(double v) { this.metrosCarroceria = v; return this; }
        public Builder metrosComMargemNvia(double v) { this.metrosComMargemNvia = v; return this; }
        public Builder metrosComMargemVenda(double v) { this.metrosComMargemVenda = v; return this; }
        public Builder veiculoSugerido(TipoVeiculo v) { this.veiculoSugerido = v; return this; }
        public Builder itensSeca(List<ItemVolumeDTO> v) { this.itensSeca = v; return this; }
        public Builder itensRefrigerado(List<ItemVolumeDTO> v) { this.itensRefrigerado = v; return this; }
        public Builder volumePorCategoria(Map<String, Double> v) { this.volumePorCategoria = v; return this; }

        public ResultadoVolumetriaDTO build() {
            ResultadoVolumetriaDTO d = new ResultadoVolumetriaDTO();
            d.volumeTotalM3 = volumeTotalM3; d.metrosCarroceria = metrosCarroceria;
            d.metrosComMargemNvia = metrosComMargemNvia; d.metrosComMargemVenda = metrosComMargemVenda;
            d.veiculoSugerido = veiculoSugerido; d.itensSeca = itensSeca;
            d.itensRefrigerado = itensRefrigerado; d.volumePorCategoria = volumePorCategoria;
            return d;
        }
    }

    public double getVolumeTotalM3() { return volumeTotalM3; }
    public double getMetrosCarroceria() { return metrosCarroceria; }
    public double getMetrosComMargemNvia() { return metrosComMargemNvia; }
    public double getMetrosComMargemVenda() { return metrosComMargemVenda; }
    public TipoVeiculo getVeiculoSugerido() { return veiculoSugerido; }
    public List<ItemVolumeDTO> getItensSeca() { return itensSeca; }
    public List<ItemVolumeDTO> getItensRefrigerado() { return itensRefrigerado; }
    public Map<String, Double> getVolumePorCategoria() { return volumePorCategoria; }

    public static class ItemVolumeDTO {
        private String codigo, descricao, categoria;
        private TipoProduto tipo;
        private int quantidade;
        private double volumeUnitario, volumeTotal, fatorAjuste, volumeAjustado;

        public ItemVolumeDTO() {}

        public static ItemVolumeDTOBuilder builder() { return new ItemVolumeDTOBuilder(); }

        public static class ItemVolumeDTOBuilder {
            private String codigo, descricao, categoria;
            private TipoProduto tipo;
            private int quantidade;
            private double volumeUnitario, volumeTotal, fatorAjuste, volumeAjustado;

            public ItemVolumeDTOBuilder codigo(String v) { this.codigo = v; return this; }
            public ItemVolumeDTOBuilder descricao(String v) { this.descricao = v; return this; }
            public ItemVolumeDTOBuilder categoria(String v) { this.categoria = v; return this; }
            public ItemVolumeDTOBuilder tipo(TipoProduto v) { this.tipo = v; return this; }
            public ItemVolumeDTOBuilder quantidade(int v) { this.quantidade = v; return this; }
            public ItemVolumeDTOBuilder volumeUnitario(double v) { this.volumeUnitario = v; return this; }
            public ItemVolumeDTOBuilder volumeTotal(double v) { this.volumeTotal = v; return this; }
            public ItemVolumeDTOBuilder fatorAjuste(double v) { this.fatorAjuste = v; return this; }
            public ItemVolumeDTOBuilder volumeAjustado(double v) { this.volumeAjustado = v; return this; }

            public ItemVolumeDTO build() {
                ItemVolumeDTO d = new ItemVolumeDTO();
                d.codigo = codigo; d.descricao = descricao; d.categoria = categoria;
                d.tipo = tipo; d.quantidade = quantidade; d.volumeUnitario = volumeUnitario;
                d.volumeTotal = volumeTotal; d.fatorAjuste = fatorAjuste; d.volumeAjustado = volumeAjustado;
                return d;
            }
        }

        public String getCodigo() { return codigo; }
        public String getDescricao() { return descricao; }
        public String getCategoria() { return categoria; }
        public TipoProduto getTipo() { return tipo; }
        public int getQuantidade() { return quantidade; }
        public double getVolumeUnitario() { return volumeUnitario; }
        public double getVolumeTotal() { return volumeTotal; }
        public double getFatorAjuste() { return fatorAjuste; }
        public double getVolumeAjustado() { return volumeAjustado; }
    }
}
