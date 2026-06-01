package com.fastariam.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "tabela_frete")
public class TabelaFrete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "cidade_id", nullable = false)
    private Cidade cidade;

    // --- Valores de frete por km ---

    /** Custo por km rodado para veículo Truck (R$/km) */
    private double freteKmTruck;

    /** Custo por km rodado para Carreta (R$/km) */
    private double freteKmCarreta;

    // --- Pedágios fixos por rota ---

    /** Valor fixo de pedágio para Truck nesta rota (R$) */
    private double pedagioTruck;

    /** Valor fixo de pedágio para Carreta nesta rota (R$) */
    private double pedagioCarreta;

    // --- Fiscal ---

    /** Alíquota de ICMS do estado de destino (%) */
    private double icmsPercent;

    // --- Controle ---

    /** Data de vigência desta tabela (quando entrou em vigor) */
    private LocalDateTime vigenciaInicio;

    /** Indica se esta é a tabela vigente para a cidade */
    private boolean ativa = true;

    @Column(length = 200)
    private String observacao;

    public TabelaFrete() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Cidade cidade;
        private double freteKmTruck, freteKmCarreta;
        private double pedagioTruck, pedagioCarreta;
        private double icmsPercent;
        private LocalDateTime vigenciaInicio;
        private boolean ativa = true;
        private String observacao;

        public Builder cidade(Cidade v)            { this.cidade = v; return this; }
        public Builder freteKmTruck(double v)      { this.freteKmTruck = v; return this; }
        public Builder freteKmCarreta(double v)    { this.freteKmCarreta = v; return this; }
        public Builder pedagioTruck(double v)      { this.pedagioTruck = v; return this; }
        public Builder pedagioCarreta(double v)    { this.pedagioCarreta = v; return this; }
        public Builder icmsPercent(double v)       { this.icmsPercent = v; return this; }
        public Builder vigenciaInicio(LocalDateTime v) { this.vigenciaInicio = v; return this; }
        public Builder ativa(boolean v)            { this.ativa = v; return this; }
        public Builder observacao(String v)        { this.observacao = v; return this; }

        public TabelaFrete build() {
            TabelaFrete t = new TabelaFrete();
            t.cidade = cidade;
            t.freteKmTruck = freteKmTruck;
            t.freteKmCarreta = freteKmCarreta;
            t.pedagioTruck = pedagioTruck;
            t.pedagioCarreta = pedagioCarreta;
            t.icmsPercent = icmsPercent;
            t.vigenciaInicio = vigenciaInicio != null ? vigenciaInicio : LocalDateTime.now();
            t.ativa = ativa;
            t.observacao = observacao;
            return t;
        }
    }

    /** Calcula o frete base para Truck dado a distância da cidade */
    public double calcularFreteBaseTruck() {
        return freteKmTruck * cidade.getDistanciaKmOrigem();
    }

    /** Calcula o frete base para Carreta dado a distância da cidade */
    public double calcularFreteBaseCarreta() {
        return freteKmCarreta * cidade.getDistanciaKmOrigem();
    }

    public Long getId()                    { return id; }
    public Cidade getCidade()              { return cidade; }
    public void setCidade(Cidade v)        { this.cidade = v; }
    public double getFreteKmTruck()        { return freteKmTruck; }
    public void setFreteKmTruck(double v)  { this.freteKmTruck = v; }
    public double getFreteKmCarreta()      { return freteKmCarreta; }
    public void setFreteKmCarreta(double v){ this.freteKmCarreta = v; }
    public double getPedagioTruck()        { return pedagioTruck; }
    public void setPedagioTruck(double v)  { this.pedagioTruck = v; }
    public double getPedagioCarreta()      { return pedagioCarreta; }
    public void setPedagioCarreta(double v){ this.pedagioCarreta = v; }
    public double getIcmsPercent()         { return icmsPercent; }
    public void setIcmsPercent(double v)   { this.icmsPercent = v; }
    public LocalDateTime getVigenciaInicio()          { return vigenciaInicio; }
    public void setVigenciaInicio(LocalDateTime v)    { this.vigenciaInicio = v; }
    public boolean isAtiva()               { return ativa; }
    public void setAtiva(boolean v)        { this.ativa = v; }
    public String getObservacao()          { return observacao; }
    public void setObservacao(String v)    { this.observacao = v; }
}
