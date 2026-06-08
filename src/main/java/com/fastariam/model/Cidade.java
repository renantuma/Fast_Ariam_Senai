package com.fastariam.model;

import jakarta.persistence.*;

/**
 * Representa uma cidade de destino — apenas sua identidade geográfica.
 *
 * <p>Os dados de tarifação (distância, pedágios, frete-base por km, etc.)
 * NÃO pertencem à cidade: eles descrevem uma rota/tarifa a partir da origem
 * (Londrina/PR) e ficam em {@link TarifaFrete}. O ICMS também não é
 * armazenado aqui — é uma regra fiscal derivada do estado.</p>
 */
@Entity
@Table(
    name = "cidades",
    uniqueConstraints = @UniqueConstraint(columnNames = {"nome", "estado"})
)
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 2)
    private String estado;

    public Cidade() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String nome;
        private String estado;

        public Builder nome(String v)   { this.nome = v;   return this; }
        public Builder estado(String v) { this.estado = v; return this; }

        public Cidade build() {
            Cidade c = new Cidade();
            c.nome = nome;
            c.estado = estado;
            return c;
        }
    }

    public Long getId()            { return id; }
    public String getNome()        { return nome; }
    public void setNome(String v)  { this.nome = v; }
    public String getEstado()      { return estado; }
    public void setEstado(String v){ this.estado = v; }

    /** Rótulo "Cidade/UF" para exibição. */
    public String getLabel() {
        return nome + "/" + estado;
    }
}
