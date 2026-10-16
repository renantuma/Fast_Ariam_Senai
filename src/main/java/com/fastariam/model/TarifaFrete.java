package com.fastariam.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Tarifa de frete de uma rota origem (Londrina/PR) → cidade de destino.
 *
 * <p>Concentra os dados que antes estavam — incorretamente — dentro de
 * {@link Cidade}: distância, valor por km e pedágios para cada tipo
 * de veículo, além de indicar se a tarifa foi gerada via API de roteirização.</p>
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

    @Column(name = "distancia_km", nullable = false)
    private double distanciaKm;

    /** Valor do frete por quilômetro rodado (R$/km) — Truck. */
    @Column(name = "valor_por_km_truck", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPorKmTruck = BigDecimal.ZERO;

    /** Valor do frete por quilômetro rodado (R$/km) — Carreta. */
    @Column(name = "valor_por_km_carreta", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPorKmCarreta = BigDecimal.ZERO;

    /** Pedágio total da rota (R$) — Truck. */
    @Column(name = "pedagio_truck", nullable = false, precision = 10, scale = 2)
    private BigDecimal pedagioTruck = BigDecimal.ZERO;

    /** Pedágio total da rota (R$) — Carreta. */
    @Column(name = "pedagio_carreta", nullable = false, precision = 10, scale = 2)
    private BigDecimal pedagioCarreta = BigDecimal.ZERO;

    /** true quando a tarifa foi gerada automaticamente por API de roteirização. */
    @Column(name = "gerada_via_api", nullable = false)
    private boolean geradaViaApi;

    public TarifaFrete() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Cidade cidade;
        private double distanciaKm;
        private BigDecimal valorPorKmTruck = BigDecimal.ZERO;
        private BigDecimal valorPorKmCarreta = BigDecimal.ZERO;
        private BigDecimal pedagioTruck = BigDecimal.ZERO;
        private BigDecimal pedagioCarreta = BigDecimal.ZERO;
        private boolean geradaViaApi;

        public Builder cidade(Cidade v)                 { this.cidade = v;             return this; }
        public Builder distanciaKm(double v)            { this.distanciaKm = v;        return this; }
        public Builder valorPorKmTruck(BigDecimal v)    { this.valorPorKmTruck = v;    return this; }
        public Builder valorPorKmCarreta(BigDecimal v)  { this.valorPorKmCarreta = v;  return this; }
        public Builder pedagioTruck(BigDecimal v)       { this.pedagioTruck = v;       return this; }
        public Builder pedagioCarreta(BigDecimal v)     { this.pedagioCarreta = v;     return this; }
        public Builder geradaViaApi(boolean v)          { this.geradaViaApi = v;       return this; }

        public TarifaFrete build() {
            TarifaFrete t = new TarifaFrete();
            t.cidade = cidade;
            t.distanciaKm = distanciaKm;
            t.valorPorKmTruck = valorPorKmTruck;
            t.valorPorKmCarreta = valorPorKmCarreta;
            t.pedagioTruck = pedagioTruck;
            t.pedagioCarreta = pedagioCarreta;
            t.geradaViaApi = geradaViaApi;
            return t;
        }
    }

    /* ---- Comportamento de domínio ---- */

    /**
     * Frete-base total para Truck (R$): valor por km × distância.
     * Não arredonda — quem consome decide o momento de arredondar.
     */
    public BigDecimal freteBaseTruck() {
        return valorPorKmTruck.multiply(BigDecimal.valueOf(distanciaKm));
    }

    /** Frete-base total para Carreta (R$): valor por km × distância. */
    public BigDecimal freteBaseCarreta() {
        return valorPorKmCarreta.multiply(BigDecimal.valueOf(distanciaKm));
    }

    /** Considera-se cadastrada quando há distância positiva definida. */
    public boolean isCadastrada() {
        return distanciaKm > 0;
    }

    /* ---- Getters / Setters ---- */

    public Long getId()                              { return id; }
    public Cidade getCidade()                        { return cidade; }
    public void setCidade(Cidade v)                  { this.cidade = v; }
    public double getDistanciaKm()                   { return distanciaKm; }
    public void setDistanciaKm(double v)             { this.distanciaKm = v; }
    public BigDecimal getValorPorKmTruck()           { return valorPorKmTruck; }
    public void setValorPorKmTruck(BigDecimal v)     { this.valorPorKmTruck = v; }
    public BigDecimal getValorPorKmCarreta()         { return valorPorKmCarreta; }
    public void setValorPorKmCarreta(BigDecimal v)   { this.valorPorKmCarreta = v; }
    public BigDecimal getPedagioTruck()              { return pedagioTruck; }
    public void setPedagioTruck(BigDecimal v)        { this.pedagioTruck = v; }
    public BigDecimal getPedagioCarreta()            { return pedagioCarreta; }
    public void setPedagioCarreta(BigDecimal v)      { this.pedagioCarreta = v; }
    public boolean isGeradaViaApi()                  { return geradaViaApi; }
    public void setGeradaViaApi(boolean v)           { this.geradaViaApi = v; }
}
