package com.fastariam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(length = 200)
    private String nomeAntigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProduto tipo;

    @Enumerated(EnumType.STRING)
    private CategoriaLinhaSeca categoria;

    /** Quantas peças cabem em 1 m³ (usado só na linha seca; volume = quantidade / pecasPorM3). */
    @Column(name = "pecas_por_m3")
    private Double pecasPorM3;

    /** Dimensões em metros (usadas só nos refrigerados; volume = comprimento × largura × altura). */
    @Column(name = "comprimento_m")
    private Double comprimento;

    @Column(name = "largura_m")
    private Double largura;

    @Column(name = "altura_m")
    private Double altura;

    private boolean ativo = true;

    public Produto() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String codigo, descricao, nomeAntigo;
        private TipoProduto tipo;
        private CategoriaLinhaSeca categoria;
        private Double pecasPorM3, comprimento, largura, altura;
        private boolean ativo = true;

        public Builder codigo(String v) { this.codigo = v; return this; }
        public Builder descricao(String v) { this.descricao = v; return this; }
        public Builder nomeAntigo(String v) { this.nomeAntigo = v; return this; }
        public Builder tipo(TipoProduto v) { this.tipo = v; return this; }
        public Builder categoria(CategoriaLinhaSeca v) { this.categoria = v; return this; }
        public Builder pecasPorM3(Double v) { this.pecasPorM3 = v; return this; }
        public Builder comprimento(Double v) { this.comprimento = v; return this; }
        public Builder largura(Double v) { this.largura = v; return this; }
        public Builder altura(Double v) { this.altura = v; return this; }
        public Builder ativo(boolean v) { this.ativo = v; return this; }

        public Produto build() {
            Produto p = new Produto();
            p.codigo = codigo; p.descricao = descricao; p.nomeAntigo = nomeAntigo;
            p.tipo = tipo; p.categoria = categoria; p.pecasPorM3 = pecasPorM3;
            p.comprimento = comprimento; p.largura = largura; p.altura = altura;
            p.ativo = ativo;
            return p;
        }
    }

    public double getVolumeUnitario() {
        if (tipo == TipoProduto.REFRIGERADO && comprimento != null && largura != null && altura != null) {
            return comprimento * largura * altura;
        }
        return 0;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String v) { this.codigo = v; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String v) { this.descricao = v; }
    public String getNomeAntigo() { return nomeAntigo; }
    public void setNomeAntigo(String v) { this.nomeAntigo = v; }
    public TipoProduto getTipo() { return tipo; }
    public void setTipo(TipoProduto v) { this.tipo = v; }
    public CategoriaLinhaSeca getCategoria() { return categoria; }
    public void setCategoria(CategoriaLinhaSeca v) { this.categoria = v; }
    public Double getPecasPorM3() { return pecasPorM3; }
    public void setPecasPorM3(Double v) { this.pecasPorM3 = v; }
    public Double getComprimento() { return comprimento; }
    public void setComprimento(Double v) { this.comprimento = v; }
    public Double getLargura() { return largura; }
    public void setLargura(Double v) { this.largura = v; }
    public Double getAltura() { return altura; }
    public void setAltura(Double v) { this.altura = v; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean v) { this.ativo = v; }
}
