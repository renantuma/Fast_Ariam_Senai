package com.fastariam.model;

import jakarta.persistence.*;

/**
 * Tarifa de frete de uma rota origem (Londrina/PR) → cidade de destino.
 *
 * <p>Concentra os dados que antes estavam — incorretamente — dentro de
 * {@link Cidade}: distância, frete-base por km e pedágios para cada tipo
 * de veículo, além da origem do dado (manual ou via API de roteirização).</p>
 *
 * <p>É um modelo rico: além de armazenar valores, sabe calcular o frete-base
 * total e dizer se já foi efetivamente cadastrada.</p>
 */
@Entity
@Table(name = "tarifas_frete")
public class TarifaFrete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Cidade de destino à qual esta tarifa se aplica (lado dono da relação). */
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cidade_id", nullable = false, unique = true)
    private Cidade cidade;

    private double distanciaKm;
    private double freteBaseKmTruck;
    private double freteBaseKmCarreta;
    private double pedagioTruck;
    private double pedagioCarreta;

    /** true quando a tarifa foi gerada automaticamente por API de roteirização. */
    private boolean origemApi;

    public TarifaFrete() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Cidade cidade;
        private double distanciaKm, freteBaseKmTruck, freteBaseKmCarreta;
        private double pedagioTruck, pedagioCarreta;
        private boolean origemApi;

        public Builder cidade(Cidade v)            { this.cidade = v;            return this; }
        public Builder distanciaKm(double v)       { this.distanciaKm = v;       return this; }
        public Builder freteBaseKmTruck(double v)  { this.freteBaseKmTruck = v;  return this; }
        public Builder freteBaseKmCarreta(double v){ this.freteBaseKmCarreta = v;return this; }
        public Builder pedagioTruck(double v)      { this.pedagioTruck = v;      return this; }
        public Builder pedagioCarreta(double v)    { this.pedagioCarreta = v;    return this; }
        public Builder origemApi(boolean v)        { this.origemApi = v;         return this; }

        public TarifaFrete build() {
            TarifaFrete t = new TarifaFrete();
            t.cidade = cidade;
            t.distanciaKm = distanciaKm;
            t.freteBaseKmTruck = freteBaseKmTruck;
            t.freteBaseKmCarreta = freteBaseKmCarreta;
            t.pedagioTruck = pedagioTruck;
            t.pedagioCarreta = pedagioCarreta;
            t.origemApi = origemApi;
            return t;
        }
    }

    /* ---- Comportamento de domínio ---- */

    /** Frete-base total para Truck (R$): valor por km × distância. */
    public double freteBaseTruck() {
        return freteBaseKmTruck * distanciaKm;
    }

    /** Frete-base total para Carreta (R$): valor por km × distância. */
    public double freteBaseCarreta() {
        return freteBaseKmCarreta * distanciaKm;
    }

    /** Considera-se cadastrada quando há distância positiva definida. */
    public boolean isCadastrada() {
        return distanciaKm > 0;
    }

    /* ---- Getters / Setters ---- */

    public Long getId()                       { return id; }
    public Cidade getCidade()                 { return cidade; }
    public void setCidade(Cidade v)           { this.cidade = v; }
    public double getDistanciaKm()            { return distanciaKm; }
    public void setDistanciaKm(double v)      { this.distanciaKm = v; }
    public double getFreteBaseKmTruck()       { return freteBaseKmTruck; }
    public void setFreteBaseKmTruck(double v) { this.freteBaseKmTruck = v; }
    public double getFreteBaseKmCarreta()     { return freteBaseKmCarreta; }
    public void setFreteBaseKmCarreta(double v){ this.freteBaseKmCarreta = v; }
    public double getPedagioTruck()           { return pedagioTruck; }
    public void setPedagioTruck(double v)     { this.pedagioTruck = v; }
    public double getPedagioCarreta()         { return pedagioCarreta; }
    public void setPedagioCarreta(double v)   { this.pedagioCarreta = v; }
    public boolean isOrigemApi()              { return origemApi; }
    public void setOrigemApi(boolean v)       { this.origemApi = v; }
}
