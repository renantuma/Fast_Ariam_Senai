package com.fastariam.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Parâmetros globais de frete (uma única linha na tabela).
 *
 * <p>Convenções de nomes: valores em reais começam com {@code custo...};
 * percentuais terminam com {@code ...Percent} (9.25 significa 9,25%);
 * medidas têm a unidade no final ({@code ...M3}, {@code ...M}).</p>
 */
@Entity
@Table(name = "configuracao_frete")
public class ConfiguracaoFrete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---- Custos de descarga (R$) ----
    @Column(name = "custo_descarga_empilhadeira", nullable = false, precision = 12, scale = 2)
    private BigDecimal custoDescargaEmpilhadeira = BigDecimal.ZERO;

    @Column(name = "custo_descarga_manual_truck", nullable = false, precision = 12, scale = 2)
    private BigDecimal custoDescargaManualTruck = BigDecimal.ZERO;

    @Column(name = "custo_descarga_manual_carreta", nullable = false, precision = 12, scale = 2)
    private BigDecimal custoDescargaManualCarreta = BigDecimal.ZERO;

    // ---- Percentuais (9.25 = 9,25%) ----
    @Column(name = "pis_cofins_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal pisCofinsPercent = new BigDecimal("9.25");

    @Column(name = "margem_comercial_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal margemComercialPercent = new BigDecimal("20.00");

    /** Percentual cobrado sobre o valor da mercadoria (seguro da carga). */
    @Column(name = "ad_valorem_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal adValoremPercent = new BigDecimal("0.30");

    // ---- Volumetria: carreta usada como referência de conversão m³ → metros ----
    /** Volume útil (m³) de uma carreta de referência. */
    @Column(name = "volume_util_carreta_m3", nullable = false)
    private double volumeUtilCarretaM3 = 60.0;

    /** Comprimento (m) da carroceria dessa carreta de referência. */
    @Column(name = "comprimento_carroceria_carreta_m", nullable = false)
    private double comprimentoCarroceriaCarretaM = 12.0;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @ManyToOne
    @JoinColumn(name = "atualizado_por_id")
    private Usuario atualizadoPor;

    public ConfiguracaoFrete() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private BigDecimal custoDescargaEmpilhadeira = BigDecimal.ZERO;
        private BigDecimal custoDescargaManualTruck = BigDecimal.ZERO;
        private BigDecimal custoDescargaManualCarreta = BigDecimal.ZERO;
        private BigDecimal pisCofinsPercent = new BigDecimal("9.25");
        private BigDecimal margemComercialPercent = new BigDecimal("20.00");
        private BigDecimal adValoremPercent = new BigDecimal("0.30");
        private double volumeUtilCarretaM3 = 60.0;
        private double comprimentoCarroceriaCarretaM = 12.0;

        public Builder custoDescargaEmpilhadeira(BigDecimal v) { this.custoDescargaEmpilhadeira = v; return this; }
        public Builder custoDescargaManualTruck(BigDecimal v) { this.custoDescargaManualTruck = v; return this; }
        public Builder custoDescargaManualCarreta(BigDecimal v) { this.custoDescargaManualCarreta = v; return this; }
        public Builder pisCofinsPercent(BigDecimal v) { this.pisCofinsPercent = v; return this; }
        public Builder margemComercialPercent(BigDecimal v) { this.margemComercialPercent = v; return this; }
        public Builder adValoremPercent(BigDecimal v) { this.adValoremPercent = v; return this; }
        public Builder volumeUtilCarretaM3(double v) { this.volumeUtilCarretaM3 = v; return this; }
        public Builder comprimentoCarroceriaCarretaM(double v) { this.comprimentoCarroceriaCarretaM = v; return this; }

        public ConfiguracaoFrete build() {
            ConfiguracaoFrete c = new ConfiguracaoFrete();
            c.custoDescargaEmpilhadeira = custoDescargaEmpilhadeira;
            c.custoDescargaManualTruck = custoDescargaManualTruck;
            c.custoDescargaManualCarreta = custoDescargaManualCarreta;
            c.pisCofinsPercent = pisCofinsPercent;
            c.margemComercialPercent = margemComercialPercent;
            c.adValoremPercent = adValoremPercent;
            c.volumeUtilCarretaM3 = volumeUtilCarretaM3;
            c.comprimentoCarroceriaCarretaM = comprimentoCarroceriaCarretaM;
            return c;
        }
    }

    @PreUpdate
    void preUpdate() { this.atualizadoEm = LocalDateTime.now(); }

    public Long getId() { return id; }
    public BigDecimal getCustoDescargaEmpilhadeira() { return custoDescargaEmpilhadeira; }
    public void setCustoDescargaEmpilhadeira(BigDecimal v) { this.custoDescargaEmpilhadeira = v; }
    public BigDecimal getCustoDescargaManualTruck() { return custoDescargaManualTruck; }
    public void setCustoDescargaManualTruck(BigDecimal v) { this.custoDescargaManualTruck = v; }
    public BigDecimal getCustoDescargaManualCarreta() { return custoDescargaManualCarreta; }
    public void setCustoDescargaManualCarreta(BigDecimal v) { this.custoDescargaManualCarreta = v; }
    public BigDecimal getPisCofinsPercent() { return pisCofinsPercent; }
    public void setPisCofinsPercent(BigDecimal v) { this.pisCofinsPercent = v; }
    public BigDecimal getMargemComercialPercent() { return margemComercialPercent; }
    public void setMargemComercialPercent(BigDecimal v) { this.margemComercialPercent = v; }
    public BigDecimal getAdValoremPercent() { return adValoremPercent; }
    public void setAdValoremPercent(BigDecimal v) { this.adValoremPercent = v; }
    public double getVolumeUtilCarretaM3() { return volumeUtilCarretaM3; }
    public void setVolumeUtilCarretaM3(double v) { this.volumeUtilCarretaM3 = v; }
    public double getComprimentoCarroceriaCarretaM() { return comprimentoCarroceriaCarretaM; }
    public void setComprimentoCarroceriaCarretaM(double v) { this.comprimentoCarroceriaCarretaM = v; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public Usuario getAtualizadoPor() { return atualizadoPor; }
    public void setAtualizadoPor(Usuario v) { this.atualizadoPor = v; }
}
