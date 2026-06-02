package com.fastariam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cidades")
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 2)
    private String estado;

    private double icmsPercent;
    private double distanciaKm;
    private double pedagioTruck;
    private double pedagioCarreta;
    private double freteBaseKmTruck;
    private double freteBaseKmCarreta;
    private boolean viaApi;

    public Cidade() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String nome, estado;
        private double icmsPercent, distanciaKm, pedagioTruck, pedagioCarreta;
        private double freteBaseKmTruck, freteBaseKmCarreta;
        private boolean viaApi;

        public Builder nome(String v) { this.nome = v; return this; }
        public Builder estado(String v) { this.estado = v; return this; }
        public Builder icmsPercent(double v) { this.icmsPercent = v; return this; }
        public Builder distanciaKm(double v) { this.distanciaKm = v; return this; }
        public Builder pedagioTruck(double v) { this.pedagioTruck = v; return this; }
        public Builder pedagioCarreta(double v) { this.pedagioCarreta = v; return this; }
        public Builder freteBaseKmTruck(double v) { this.freteBaseKmTruck = v; return this; }
        public Builder freteBaseKmCarreta(double v) { this.freteBaseKmCarreta = v; return this; }
        public Builder viaApi(boolean v) { this.viaApi = v; return this; }

        public Cidade build() {
            Cidade c = new Cidade();
            c.nome = nome; c.estado = estado; c.icmsPercent = icmsPercent;
            c.distanciaKm = distanciaKm; c.pedagioTruck = pedagioTruck;
            c.pedagioCarreta = pedagioCarreta; c.freteBaseKmTruck = freteBaseKmTruck;
            c.freteBaseKmCarreta = freteBaseKmCarreta; c.viaApi = viaApi;
            return c;
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String v) { this.nome = v; }
    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }
    public double getIcmsPercent() { return icmsPercent; }
    public void setIcmsPercent(double v) { this.icmsPercent = v; }
    public double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(double v) { this.distanciaKm = v; }
    public double getPedagioTruck() { return pedagioTruck; }
    public void setPedagioTruck(double v) { this.pedagioTruck = v; }
    public double getPedagioCarreta() { return pedagioCarreta; }
    public void setPedagioCarreta(double v) { this.pedagioCarreta = v; }
    public double getFreteBaseKmTruck() { return freteBaseKmTruck; }
    public void setFreteBaseKmTruck(double v) { this.freteBaseKmTruck = v; }
    public double getFreteBaseKmCarreta() { return freteBaseKmCarreta; }
    public void setFreteBaseKmCarreta(double v) { this.freteBaseKmCarreta = v; }
    public boolean isViaApi() { return viaApi; }
    public void setViaApi(boolean v) { this.viaApi = v; }
    public boolean isCadastrada() { return distanciaKm > 0; }
}
