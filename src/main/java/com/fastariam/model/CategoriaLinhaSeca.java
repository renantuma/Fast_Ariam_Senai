package com.fastariam.model;

public enum CategoriaLinhaSeca {
    GONDOLAS("Gôndolas (LSG)", 1.40),
    MOBILIARIOS("Mobiliários", 1.20),
    RACK_SLIM("Rack Slim", 1.20),
    CHECKOUTS("Checkouts", 1.00),
    PORTA_PALLETS_MONTADOS("Porta Pallets - Montados", 1.00),
    PORTA_PALLETS_DESMONTADOS("Porta Pallets - Desmontados", 4.00);

    private final String descricao;
    private final double fatorAjuste;

    CategoriaLinhaSeca(String descricao, double fatorAjuste) {
        this.descricao = descricao;
        this.fatorAjuste = fatorAjuste;
    }

    public String getDescricao() { return descricao; }
    public double getFatorAjuste() { return fatorAjuste; }
}
