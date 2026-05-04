package com.fastariam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracao_frete")
public class ConfiguracaoFrete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double descargarEmpilhadeira;
    private double descargarManualTruck;
    private double descargarManualCarreta;
    private double pisCofinsPercent = 9.25;
    private double margemComercialPercent = 20.0;
    private double adValoremDefault = 0.3;
    private double capacidadeCarretaM3 = 60.0;
    private double capacidadeContainerM3 = 45.0;
    private double fatorMetrosCarroceria = 12.0;
    private LocalDateTime atualizadoEm;

    @ManyToOne
    @JoinColumn(name = "atualizado_por")
    private Usuario atualizadoPor;

    public ConfiguracaoFrete() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private double descargarEmpilhadeira, descargarManualTruck, descargarManualCarreta;
        private double pisCofinsPercent = 9.25, margemComercialPercent = 20.0;
        private double adValoremDefault = 0.3, capacidadeCarretaM3 = 60.0;
        private double capacidadeContainerM3 = 45.0, fatorMetrosCarroceria = 12.0;

        public Builder descargarEmpilhadeira(double v) { this.descargarEmpilhadeira = v; return this; }
        public Builder descargarManualTruck(double v) { this.descargarManualTruck = v; return this; }
        public Builder descargarManualCarreta(double v) { this.descargarManualCarreta = v; return this; }
        public Builder pisCofinsPercent(double v) { this.pisCofinsPercent = v; return this; }
        public Builder margemComercialPercent(double v) { this.margemComercialPercent = v; return this; }
        public Builder adValoremDefault(double v) { this.adValoremDefault = v; return this; }
        public Builder capacidadeCarretaM3(double v) { this.capacidadeCarretaM3 = v; return this; }
        public Builder capacidadeContainerM3(double v) { this.capacidadeContainerM3 = v; return this; }
        public Builder fatorMetrosCarroceria(double v) { this.fatorMetrosCarroceria = v; return this; }

        public ConfiguracaoFrete build() {
            ConfiguracaoFrete c = new ConfiguracaoFrete();
            c.descargarEmpilhadeira = descargarEmpilhadeira;
            c.descargarManualTruck = descargarManualTruck;
            c.descargarManualCarreta = descargarManualCarreta;
            c.pisCofinsPercent = pisCofinsPercent;
            c.margemComercialPercent = margemComercialPercent;
            c.adValoremDefault = adValoremDefault;
            c.capacidadeCarretaM3 = capacidadeCarretaM3;
            c.capacidadeContainerM3 = capacidadeContainerM3;
            c.fatorMetrosCarroceria = fatorMetrosCarroceria;
            return c;
        }
    }

    @PreUpdate
    void preUpdate() { this.atualizadoEm = LocalDateTime.now(); }

    public Long getId() { return id; }
    public double getDescargarEmpilhadeira() { return descargarEmpilhadeira; }
    public void setDescargarEmpilhadeira(double v) { this.descargarEmpilhadeira = v; }
    public double getDescargarManualTruck() { return descargarManualTruck; }
    public void setDescargarManualTruck(double v) { this.descargarManualTruck = v; }
    public double getDescargarManualCarreta() { return descargarManualCarreta; }
    public void setDescargarManualCarreta(double v) { this.descargarManualCarreta = v; }
    public double getPisCofinsPercent() { return pisCofinsPercent; }
    public void setPisCofinsPercent(double v) { this.pisCofinsPercent = v; }
    public double getMargemComercialPercent() { return margemComercialPercent; }
    public void setMargemComercialPercent(double v) { this.margemComercialPercent = v; }
    public double getAdValoremDefault() { return adValoremDefault; }
    public void setAdValoremDefault(double v) { this.adValoremDefault = v; }
    public double getCapacidadeCarretaM3() { return capacidadeCarretaM3; }
    public void setCapacidadeCarretaM3(double v) { this.capacidadeCarretaM3 = v; }
    public double getCapacidadeContainerM3() { return capacidadeContainerM3; }
    public void setCapacidadeContainerM3(double v) { this.capacidadeContainerM3 = v; }
    public double getFatorMetrosCarroceria() { return fatorMetrosCarroceria; }
    public void setFatorMetrosCarroceria(double v) { this.fatorMetrosCarroceria = v; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public Usuario getAtualizadoPor() { return atualizadoPor; }
    public void setAtualizadoPor(Usuario v) { this.atualizadoPor = v; }
}
