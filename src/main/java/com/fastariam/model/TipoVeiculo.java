package com.fastariam.model;

public enum TipoVeiculo {
    VUC("VUC", 6.0, "Até 6 m³"),
    TOCO("Toco", 18.0, "Até 18 m³"),
    TRUCK("Truck", 40.0, "Até 40 m³"),
    CARRETA("Carreta Baú", 80.0, "Até 80 m³"),
    BITREM("Bi-trem", 120.0, "Acima de 80 m³");

    private final String descricao;
    private final double capacidadeM3;
    private final String observacao;

    TipoVeiculo(String descricao, double capacidadeM3, String observacao) {
        this.descricao = descricao;
        this.capacidadeM3 = capacidadeM3;
        this.observacao = observacao;
    }

    public String getDescricao() { return descricao; }
    public double getCapacidadeM3() { return capacidadeM3; }
    public String getObservacao() { return observacao; }

    public static TipoVeiculo sugerirPorVolume(double volumeM3) {
        for (TipoVeiculo v : values()) {
            if (volumeM3 <= v.capacidadeM3) return v;
        }
        return BITREM;
    }
}
