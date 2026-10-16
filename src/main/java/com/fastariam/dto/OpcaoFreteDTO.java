package com.fastariam.dto;

import java.math.BigDecimal;

/**
 * Resultado do cálculo de frete para <b>um</b> veículo (valores em R$, já
 * arredondados para centavos). É imutável.
 */
public class OpcaoFreteDTO {

    private final BigDecimal freteBase;
    private final BigDecimal pedagio;
    private final BigDecimal custoDescarga;
    private final BigDecimal adValorem;
    private final BigDecimal subtotal;
    private final BigDecimal impostos;
    private final BigDecimal margemComercialValor;
    private final BigDecimal total;

    public OpcaoFreteDTO(BigDecimal freteBase, BigDecimal pedagio, BigDecimal custoDescarga,
                         BigDecimal adValorem, BigDecimal subtotal, BigDecimal impostos,
                         BigDecimal margemComercialValor, BigDecimal total) {
        this.freteBase = freteBase;
        this.pedagio = pedagio;
        this.custoDescarga = custoDescarga;
        this.adValorem = adValorem;
        this.subtotal = subtotal;
        this.impostos = impostos;
        this.margemComercialValor = margemComercialValor;
        this.total = total;
    }

    public BigDecimal getFreteBase() { return freteBase; }
    public BigDecimal getPedagio() { return pedagio; }
    public BigDecimal getCustoDescarga() { return custoDescarga; }
    public BigDecimal getAdValorem() { return adValorem; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getImpostos() { return impostos; }
    public BigDecimal getMargemComercialValor() { return margemComercialValor; }
    public BigDecimal getTotal() { return total; }
}
