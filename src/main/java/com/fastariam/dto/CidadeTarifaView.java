package com.fastariam.dto;

import com.fastariam.model.Cidade;
import com.fastariam.model.TarifaFrete;

import java.math.BigDecimal;

/**
 * Projeção somente-leitura para a tela de cidades: junta a identidade da
 * cidade, sua tarifa de frete (pode ser nula se ainda não cadastrada) e o
 * ICMS derivado do estado. Existe apenas para a camada de apresentação.
 */
public record CidadeTarifaView(Cidade cidade, TarifaFrete tarifa, double icmsPercent) {

    public Long getId()        { return cidade.getId(); }
    public String getNome()    { return cidade.getNome(); }
    public String getEstado()  { return cidade.getEstado(); }
    public boolean isCadastrada()   { return tarifa != null && tarifa.isCadastrada(); }
    public boolean isGeradaViaApi() { return tarifa != null && tarifa.isGeradaViaApi(); }

    public double getDistanciaKm()            { return tarifa != null ? tarifa.getDistanciaKm() : 0; }
    public BigDecimal getValorPorKmTruck()    { return tarifa != null ? tarifa.getValorPorKmTruck() : BigDecimal.ZERO; }
    public BigDecimal getValorPorKmCarreta()  { return tarifa != null ? tarifa.getValorPorKmCarreta() : BigDecimal.ZERO; }
    public BigDecimal getPedagioTruck()       { return tarifa != null ? tarifa.getPedagioTruck() : BigDecimal.ZERO; }
    public BigDecimal getPedagioCarreta()     { return tarifa != null ? tarifa.getPedagioCarreta() : BigDecimal.ZERO; }
    public double getIcmsPercent()            { return icmsPercent; }
}
