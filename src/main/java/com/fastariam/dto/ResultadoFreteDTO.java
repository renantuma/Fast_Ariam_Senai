package com.fastariam.dto;

import com.fastariam.model.TipoVeiculo;

public class ResultadoFreteDTO {

    private String cidadeDestino;
    private String estadoDestino;
    private double distanciaKm;

    private double freteBaseTruck;
    private double pedagioTruck;
    private double descargarTruck;
    private double adValoremTruck;
    private double impostosTruck;
    private double subtotalTruck;
    private double margemTruck;
    private double totalTruck;

    private double freteBaseCarreta;
    private double pedagioCarreta;
    private double descargarCarreta;
    private double adValoremCarreta;
    private double impostosCarreta;
    private double subtotalCarreta;
    private double margemCarreta;
    private double totalCarreta;

    private double icmsPercent;
    private double pisCofinsPercent;
    private double margemComercialPercent;
    private double fatorDescargaCliente;
    private TipoVeiculo veiculoRecomendado;
    private boolean cidadeViaApi;

    public ResultadoFreteDTO() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String cidadeDestino, estadoDestino;
        private double distanciaKm;
        private double freteBaseTruck, pedagioTruck, descargarTruck, adValoremTruck;
        private double impostosTruck, subtotalTruck, margemTruck, totalTruck;
        private double freteBaseCarreta, pedagioCarreta, descargarCarreta, adValoremCarreta;
        private double impostosCarreta, subtotalCarreta, margemCarreta, totalCarreta;
        private double icmsPercent, pisCofinsPercent, margemComercialPercent, fatorDescargaCliente;
        private TipoVeiculo veiculoRecomendado;
        private boolean cidadeViaApi;

        public Builder cidadeDestino(String v) { this.cidadeDestino = v; return this; }
        public Builder estadoDestino(String v) { this.estadoDestino = v; return this; }
        public Builder distanciaKm(double v) { this.distanciaKm = v; return this; }
        public Builder freteBaseTruck(double v) { this.freteBaseTruck = v; return this; }
        public Builder pedagioTruck(double v) { this.pedagioTruck = v; return this; }
        public Builder descargarTruck(double v) { this.descargarTruck = v; return this; }
        public Builder adValoremTruck(double v) { this.adValoremTruck = v; return this; }
        public Builder impostosTruck(double v) { this.impostosTruck = v; return this; }
        public Builder subtotalTruck(double v) { this.subtotalTruck = v; return this; }
        public Builder margemTruck(double v) { this.margemTruck = v; return this; }
        public Builder totalTruck(double v) { this.totalTruck = v; return this; }
        public Builder freteBaseCarreta(double v) { this.freteBaseCarreta = v; return this; }
        public Builder pedagioCarreta(double v) { this.pedagioCarreta = v; return this; }
        public Builder descargarCarreta(double v) { this.descargarCarreta = v; return this; }
        public Builder adValoremCarreta(double v) { this.adValoremCarreta = v; return this; }
        public Builder impostosCarreta(double v) { this.impostosCarreta = v; return this; }
        public Builder subtotalCarreta(double v) { this.subtotalCarreta = v; return this; }
        public Builder margemCarreta(double v) { this.margemCarreta = v; return this; }
        public Builder totalCarreta(double v) { this.totalCarreta = v; return this; }
        public Builder icmsPercent(double v) { this.icmsPercent = v; return this; }
        public Builder pisCofinsPercent(double v) { this.pisCofinsPercent = v; return this; }
        public Builder margemComercialPercent(double v) { this.margemComercialPercent = v; return this; }
        public Builder fatorDescargaCliente(double v) { this.fatorDescargaCliente = v; return this; }
        public Builder veiculoRecomendado(TipoVeiculo v) { this.veiculoRecomendado = v; return this; }
        public Builder cidadeViaApi(boolean v) { this.cidadeViaApi = v; return this; }

        public ResultadoFreteDTO build() {
            ResultadoFreteDTO d = new ResultadoFreteDTO();
            d.cidadeDestino = cidadeDestino; d.estadoDestino = estadoDestino;
            d.distanciaKm = distanciaKm;
            d.freteBaseTruck = freteBaseTruck; d.pedagioTruck = pedagioTruck;
            d.descargarTruck = descargarTruck; d.adValoremTruck = adValoremTruck;
            d.impostosTruck = impostosTruck; d.subtotalTruck = subtotalTruck;
            d.margemTruck = margemTruck; d.totalTruck = totalTruck;
            d.freteBaseCarreta = freteBaseCarreta; d.pedagioCarreta = pedagioCarreta;
            d.descargarCarreta = descargarCarreta; d.adValoremCarreta = adValoremCarreta;
            d.impostosCarreta = impostosCarreta; d.subtotalCarreta = subtotalCarreta;
            d.margemCarreta = margemCarreta; d.totalCarreta = totalCarreta;
            d.icmsPercent = icmsPercent; d.pisCofinsPercent = pisCofinsPercent;
            d.margemComercialPercent = margemComercialPercent;
            d.fatorDescargaCliente = fatorDescargaCliente;
            d.veiculoRecomendado = veiculoRecomendado; d.cidadeViaApi = cidadeViaApi;
            return d;
        }
    }

    public String getCidadeDestino() { return cidadeDestino; }
    public void setCidadeDestino(String v) { this.cidadeDestino = v; }
    public String getEstadoDestino() { return estadoDestino; }
    public void setEstadoDestino(String v) { this.estadoDestino = v; }
    public double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(double v) { this.distanciaKm = v; }
    public double getFreteBaseTruck() { return freteBaseTruck; }
    public void setFreteBaseTruck(double v) { this.freteBaseTruck = v; }
    public double getPedagioTruck() { return pedagioTruck; }
    public void setPedagioTruck(double v) { this.pedagioTruck = v; }
    public double getDescargarTruck() { return descargarTruck; }
    public void setDescargarTruck(double v) { this.descargarTruck = v; }
    public double getAdValoremTruck() { return adValoremTruck; }
    public void setAdValoremTruck(double v) { this.adValoremTruck = v; }
    public double getImpostosTruck() { return impostosTruck; }
    public void setImpostosTruck(double v) { this.impostosTruck = v; }
    public double getSubtotalTruck() { return subtotalTruck; }
    public void setSubtotalTruck(double v) { this.subtotalTruck = v; }
    public double getMargemTruck() { return margemTruck; }
    public void setMargemTruck(double v) { this.margemTruck = v; }
    public double getTotalTruck() { return totalTruck; }
    public void setTotalTruck(double v) { this.totalTruck = v; }
    public double getFreteBaseCarreta() { return freteBaseCarreta; }
    public void setFreteBaseCarreta(double v) { this.freteBaseCarreta = v; }
    public double getPedagioCarreta() { return pedagioCarreta; }
    public void setPedagioCarreta(double v) { this.pedagioCarreta = v; }
    public double getDescargarCarreta() { return descargarCarreta; }
    public void setDescargarCarreta(double v) { this.descargarCarreta = v; }
    public double getAdValoremCarreta() { return adValoremCarreta; }
    public void setAdValoremCarreta(double v) { this.adValoremCarreta = v; }
    public double getImpostosCarreta() { return impostosCarreta; }
    public void setImpostosCarreta(double v) { this.impostosCarreta = v; }
    public double getSubtotalCarreta() { return subtotalCarreta; }
    public void setSubtotalCarreta(double v) { this.subtotalCarreta = v; }
    public double getMargemCarreta() { return margemCarreta; }
    public void setMargemCarreta(double v) { this.margemCarreta = v; }
    public double getTotalCarreta() { return totalCarreta; }
    public void setTotalCarreta(double v) { this.totalCarreta = v; }
    public double getIcmsPercent() { return icmsPercent; }
    public void setIcmsPercent(double v) { this.icmsPercent = v; }
    public double getPisCofinsPercent() { return pisCofinsPercent; }
    public void setPisCofinsPercent(double v) { this.pisCofinsPercent = v; }
    public double getMargemComercialPercent() { return margemComercialPercent; }
    public void setMargemComercialPercent(double v) { this.margemComercialPercent = v; }
    public double getFatorDescargaCliente() { return fatorDescargaCliente; }
    public void setFatorDescargaCliente(double v) { this.fatorDescargaCliente = v; }
    public TipoVeiculo getVeiculoRecomendado() { return veiculoRecomendado; }
    public void setVeiculoRecomendado(TipoVeiculo v) { this.veiculoRecomendado = v; }
    public boolean isCidadeViaApi() { return cidadeViaApi; }
    public void setCidadeViaApi(boolean v) { this.cidadeViaApi = v; }
}
