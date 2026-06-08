package com.fastariam.dto;

import com.fastariam.model.Cidade;
import com.fastariam.model.TarifaFrete;

/**
 * Projeção somente-leitura para a tela de cidades: junta a identidade da
 * cidade, sua tarifa de frete (pode ser nula se ainda não cadastrada) e o
 * ICMS derivado do estado. Existe apenas para a camada de apresentação.
 */
public record CidadeTarifaView(Cidade cidade, TarifaFrete tarifa, double icmsPercent) {

    public Long getId()        { return cidade.getId(); }
    public String getNome()    { return cidade.getNome(); }
    public String getEstado()  { return cidade.getEstado(); }
    public boolean isCadastrada() { return tarifa != null && tarifa.isCadastrada(); }
    public boolean isOrigemApi()  { return tarifa != null && tarifa.isOrigemApi(); }

    public double getDistanciaKm()        { return tarifa != null ? tarifa.getDistanciaKm() : 0; }
    public double getFreteBaseKmTruck()   { return tarifa != null ? tarifa.getFreteBaseKmTruck() : 0; }
    public double getFreteBaseKmCarreta() { return tarifa != null ? tarifa.getFreteBaseKmCarreta() : 0; }
    public double getPedagioTruck()       { return tarifa != null ? tarifa.getPedagioTruck() : 0; }
    public double getPedagioCarreta()     { return tarifa != null ? tarifa.getPedagioCarreta() : 0; }
    public double getIcmsPercent()        { return icmsPercent; }
}
