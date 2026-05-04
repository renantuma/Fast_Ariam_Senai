package com.fastariam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "propostas")
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    private double freteBaseTruck;
    private double freteBaseCarreta;
    private double pedagioTruck;
    private double pedagioCarreta;
    private double descargarManualTruck;
    private double descargarManualCarreta;
    private double adValorem;
    private double valorMercadoria;
    private double icmsPercent;
    private double pisCofinsPercent = 9.25;
    private double margemComercial = 20.0;
    private double totalFreteTruck;
    private double totalFreteCarreta;

    @Enumerated(EnumType.STRING)
    private TipoVeiculo veiculoEscolhido;

    private double freteTotal;
    private double ajusteManual;

    @Column(length = 300)
    private String justificativaAjuste;

    @Column(length = 300)
    private String arquivoPdf;

    private LocalDateTime geradaEm = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "gerada_por")
    private Usuario geradaPor;

    public Proposta() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Pedido pedido;
        private double freteBaseTruck, freteBaseCarreta, pedagioTruck, pedagioCarreta;
        private double descargarManualTruck, descargarManualCarreta, adValorem, valorMercadoria;
        private double icmsPercent, pisCofinsPercent = 9.25, margemComercial = 20.0;
        private double totalFreteTruck, totalFreteCarreta, freteTotal, ajusteManual;
        private TipoVeiculo veiculoEscolhido;
        private String justificativaAjuste, arquivoPdf;
        private LocalDateTime geradaEm;
        private Usuario geradaPor;

        public Builder pedido(Pedido v) { this.pedido = v; return this; }
        public Builder freteBaseTruck(double v) { this.freteBaseTruck = v; return this; }
        public Builder freteBaseCarreta(double v) { this.freteBaseCarreta = v; return this; }
        public Builder pedagioTruck(double v) { this.pedagioTruck = v; return this; }
        public Builder pedagioCarreta(double v) { this.pedagioCarreta = v; return this; }
        public Builder descargarManualTruck(double v) { this.descargarManualTruck = v; return this; }
        public Builder descargarManualCarreta(double v) { this.descargarManualCarreta = v; return this; }
        public Builder adValorem(double v) { this.adValorem = v; return this; }
        public Builder valorMercadoria(double v) { this.valorMercadoria = v; return this; }
        public Builder icmsPercent(double v) { this.icmsPercent = v; return this; }
        public Builder pisCofinsPercent(double v) { this.pisCofinsPercent = v; return this; }
        public Builder margemComercial(double v) { this.margemComercial = v; return this; }
        public Builder totalFreteTruck(double v) { this.totalFreteTruck = v; return this; }
        public Builder totalFreteCarreta(double v) { this.totalFreteCarreta = v; return this; }
        public Builder veiculoEscolhido(TipoVeiculo v) { this.veiculoEscolhido = v; return this; }
        public Builder freteTotal(double v) { this.freteTotal = v; return this; }
        public Builder ajusteManual(double v) { this.ajusteManual = v; return this; }
        public Builder justificativaAjuste(String v) { this.justificativaAjuste = v; return this; }
        public Builder arquivoPdf(String v) { this.arquivoPdf = v; return this; }
        public Builder geradaEm(LocalDateTime v) { this.geradaEm = v; return this; }
        public Builder geradaPor(Usuario v) { this.geradaPor = v; return this; }

        public Proposta build() {
            Proposta p = new Proposta();
            p.pedido = pedido; p.freteBaseTruck = freteBaseTruck; p.freteBaseCarreta = freteBaseCarreta;
            p.pedagioTruck = pedagioTruck; p.pedagioCarreta = pedagioCarreta;
            p.descargarManualTruck = descargarManualTruck; p.descargarManualCarreta = descargarManualCarreta;
            p.adValorem = adValorem; p.valorMercadoria = valorMercadoria;
            p.icmsPercent = icmsPercent; p.pisCofinsPercent = pisCofinsPercent;
            p.margemComercial = margemComercial; p.totalFreteTruck = totalFreteTruck;
            p.totalFreteCarreta = totalFreteCarreta; p.veiculoEscolhido = veiculoEscolhido;
            p.freteTotal = freteTotal; p.ajusteManual = ajusteManual;
            p.justificativaAjuste = justificativaAjuste; p.arquivoPdf = arquivoPdf;
            p.geradaEm = geradaEm != null ? geradaEm : LocalDateTime.now();
            p.geradaPor = geradaPor;
            return p;
        }
    }

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido v) { this.pedido = v; }
    public double getFreteBaseTruck() { return freteBaseTruck; }
    public void setFreteBaseTruck(double v) { this.freteBaseTruck = v; }
    public double getFreteBaseCarreta() { return freteBaseCarreta; }
    public void setFreteBaseCarreta(double v) { this.freteBaseCarreta = v; }
    public double getPedagioTruck() { return pedagioTruck; }
    public void setPedagioTruck(double v) { this.pedagioTruck = v; }
    public double getPedagioCarreta() { return pedagioCarreta; }
    public void setPedagioCarreta(double v) { this.pedagioCarreta = v; }
    public double getDescargarManualTruck() { return descargarManualTruck; }
    public void setDescargarManualTruck(double v) { this.descargarManualTruck = v; }
    public double getDescargarManualCarreta() { return descargarManualCarreta; }
    public void setDescargarManualCarreta(double v) { this.descargarManualCarreta = v; }
    public double getAdValorem() { return adValorem; }
    public void setAdValorem(double v) { this.adValorem = v; }
    public double getValorMercadoria() { return valorMercadoria; }
    public void setValorMercadoria(double v) { this.valorMercadoria = v; }
    public double getIcmsPercent() { return icmsPercent; }
    public void setIcmsPercent(double v) { this.icmsPercent = v; }
    public double getPisCofinsPercent() { return pisCofinsPercent; }
    public void setPisCofinsPercent(double v) { this.pisCofinsPercent = v; }
    public double getMargemComercial() { return margemComercial; }
    public void setMargemComercial(double v) { this.margemComercial = v; }
    public double getTotalFreteTruck() { return totalFreteTruck; }
    public void setTotalFreteTruck(double v) { this.totalFreteTruck = v; }
    public double getTotalFreteCarreta() { return totalFreteCarreta; }
    public void setTotalFreteCarreta(double v) { this.totalFreteCarreta = v; }
    public TipoVeiculo getVeiculoEscolhido() { return veiculoEscolhido; }
    public void setVeiculoEscolhido(TipoVeiculo v) { this.veiculoEscolhido = v; }
    public double getFreteTotal() { return freteTotal; }
    public void setFreteTotal(double v) { this.freteTotal = v; }
    public double getAjusteManual() { return ajusteManual; }
    public void setAjusteManual(double v) { this.ajusteManual = v; }
    public String getJustificativaAjuste() { return justificativaAjuste; }
    public void setJustificativaAjuste(String v) { this.justificativaAjuste = v; }
    public String getArquivoPdf() { return arquivoPdf; }
    public void setArquivoPdf(String v) { this.arquivoPdf = v; }
    public LocalDateTime getGeradaEm() { return geradaEm; }
    public void setGeradaEm(LocalDateTime v) { this.geradaEm = v; }
    public Usuario getGeradaPor() { return geradaPor; }
    public void setGeradaPor(Usuario v) { this.geradaPor = v; }
}
