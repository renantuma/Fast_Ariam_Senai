package com.fastariam.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Representa uma cidade brasileira com seus dados geográficos.
 * Dados comerciais de frete ficam em TabelaFrete.
 */
@Entity
@Table(name = "cidades", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nome", "estado"})
})
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 2)
    private String estado;

    /** Região geográfica (ex: Sudeste, Sul, Norte) */
    @Column(length = 50)
    private String regiao;

    /** Distância em km a partir de Londrina-PR (origem padrão de expedição) */
    private double distanciaKmOrigem;

    /** Latitude para integração com APIs de roteirização */
    private Double latitude;

    /** Longitude para integração com APIs de roteirização */
    private Double longitude;

    /** Indica se a distância foi obtida via API ou cadastro manual */
    private boolean distanciaViaApi;

    public Cidade() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String nome, estado, regiao;
        private double distanciaKmOrigem;
        private Double latitude, longitude;
        private boolean distanciaViaApi;

        public Builder nome(String v)              { this.nome = v; return this; }
        public Builder estado(String v)            { this.estado = v; return this; }
        public Builder regiao(String v)            { this.regiao = v; return this; }
        public Builder distanciaKmOrigem(double v) { this.distanciaKmOrigem = v; return this; }
        public Builder latitude(Double v)          { this.latitude = v; return this; }
        public Builder longitude(Double v)         { this.longitude = v; return this; }
        public Builder distanciaViaApi(boolean v)  { this.distanciaViaApi = v; return this; }

        public Cidade build() {
            Cidade c = new Cidade();
            c.nome = nome;
            c.estado = estado;
            c.regiao = regiao;
            c.distanciaKmOrigem = distanciaKmOrigem;
            c.latitude = latitude;
            c.longitude = longitude;
            c.distanciaViaApi = distanciaViaApi;
            return c;
        }
    }

    public Long getId()                       { return id; }
    public String getNome()                   { return nome; }
    public void setNome(String v)             { this.nome = v; }
    public String getEstado()                 { return estado; }
    public void setEstado(String v)           { this.estado = v; }
    public String getRegiao()                 { return regiao; }
    public void setRegiao(String v)           { this.regiao = v; }
    public double getDistanciaKmOrigem()      { return distanciaKmOrigem; }
    public void setDistanciaKmOrigem(double v){ this.distanciaKmOrigem = v; }
    public Double getLatitude()               { return latitude; }
    public void setLatitude(Double v)         { this.latitude = v; }
    public Double getLongitude()              { return longitude; }
    public void setLongitude(Double v)        { this.longitude = v; }
    public boolean isDistanciaViaApi()        { return distanciaViaApi; }
    public void setDistanciaViaApi(boolean v) { this.distanciaViaApi = v; }

    @Override
    public String toString() { return nome + "/" + estado; }
}
