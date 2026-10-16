package com.fastariam.dto;

import com.fastariam.model.TipoVeiculo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Resultado completo do cálculo de frete para um pedido: dados da rota,
 * alíquotas usadas e uma {@link OpcaoFreteDTO} por veículo precificado
 * (hoje Truck e Carreta).
 */
public class ResultadoFreteDTO {

    private String cidadeDestino;
    private String estadoDestino;
    private double distanciaKm;
    private BigDecimal valorMercadoria;

    private OpcaoFreteDTO truck;
    private OpcaoFreteDTO carreta;

    private BigDecimal icmsPercent;
    private BigDecimal pisCofinsPercent;
    private BigDecimal margemComercialPercent;
    private BigDecimal adValoremPercent;

    private double fatorDescargaCliente;
    private TipoVeiculo veiculoRecomendado;

    /** true quando a tarifa da rota foi gerada por API de roteirização (não estava cadastrada). */
    private boolean tarifaGeradaViaApi;

    public ResultadoFreteDTO() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String cidadeDestino, estadoDestino;
        private double distanciaKm;
        private BigDecimal valorMercadoria;
        private OpcaoFreteDTO truck, carreta;
        private BigDecimal icmsPercent, pisCofinsPercent, margemComercialPercent, adValoremPercent;
        private double fatorDescargaCliente;
        private TipoVeiculo veiculoRecomendado;
        private boolean tarifaGeradaViaApi;

        public Builder cidadeDestino(String v) { this.cidadeDestino = v; return this; }
        public Builder estadoDestino(String v) { this.estadoDestino = v; return this; }
        public Builder distanciaKm(double v) { this.distanciaKm = v; return this; }
        public Builder valorMercadoria(BigDecimal v) { this.valorMercadoria = v; return this; }
        public Builder truck(OpcaoFreteDTO v) { this.truck = v; return this; }
        public Builder carreta(OpcaoFreteDTO v) { this.carreta = v; return this; }
        public Builder icmsPercent(BigDecimal v) { this.icmsPercent = v; return this; }
        public Builder pisCofinsPercent(BigDecimal v) { this.pisCofinsPercent = v; return this; }
        public Builder margemComercialPercent(BigDecimal v) { this.margemComercialPercent = v; return this; }
        public Builder adValoremPercent(BigDecimal v) { this.adValoremPercent = v; return this; }
        public Builder fatorDescargaCliente(double v) { this.fatorDescargaCliente = v; return this; }
        public Builder veiculoRecomendado(TipoVeiculo v) { this.veiculoRecomendado = v; return this; }
        public Builder tarifaGeradaViaApi(boolean v) { this.tarifaGeradaViaApi = v; return this; }

        public ResultadoFreteDTO build() {
            ResultadoFreteDTO d = new ResultadoFreteDTO();
            d.cidadeDestino = cidadeDestino; d.estadoDestino = estadoDestino;
            d.distanciaKm = distanciaKm; d.valorMercadoria = valorMercadoria;
            d.truck = truck; d.carreta = carreta;
            d.icmsPercent = icmsPercent; d.pisCofinsPercent = pisCofinsPercent;
            d.margemComercialPercent = margemComercialPercent; d.adValoremPercent = adValoremPercent;
            d.fatorDescargaCliente = fatorDescargaCliente;
            d.veiculoRecomendado = veiculoRecomendado; d.tarifaGeradaViaApi = tarifaGeradaViaApi;
            return d;
        }
    }

    /** As opções precificadas, na ordem de exibição (Truck, depois Carreta). */
    public Map<TipoVeiculo, OpcaoFreteDTO> getOpcoes() {
        Map<TipoVeiculo, OpcaoFreteDTO> opcoes = new LinkedHashMap<>();
        opcoes.put(TipoVeiculo.TRUCK, truck);
        opcoes.put(TipoVeiculo.CARRETA, carreta);
        return opcoes;
    }

    /**
     * Opção de preço aplicável ao veículo escolhido.
     *
     * <p>Regra atual (mantida do sistema original): só existem preços para
     * Truck e Carreta; Carreta usa o preço de carreta e qualquer outro veículo
     * usa o preço de Truck.</p>
     */
    public OpcaoFreteDTO opcaoPara(TipoVeiculo veiculo) {
        return veiculo == TipoVeiculo.CARRETA ? carreta : truck;
    }

    public String getCidadeDestino() { return cidadeDestino; }
    public String getEstadoDestino() { return estadoDestino; }
    public double getDistanciaKm() { return distanciaKm; }
    public BigDecimal getValorMercadoria() { return valorMercadoria; }
    public OpcaoFreteDTO getTruck() { return truck; }
    public OpcaoFreteDTO getCarreta() { return carreta; }
    public BigDecimal getIcmsPercent() { return icmsPercent; }
    public BigDecimal getPisCofinsPercent() { return pisCofinsPercent; }
    public BigDecimal getMargemComercialPercent() { return margemComercialPercent; }
    public BigDecimal getAdValoremPercent() { return adValoremPercent; }
    public double getFatorDescargaCliente() { return fatorDescargaCliente; }
    public TipoVeiculo getVeiculoRecomendado() { return veiculoRecomendado; }
    public boolean isTarifaGeradaViaApi() { return tarifaGeradaViaApi; }
}
