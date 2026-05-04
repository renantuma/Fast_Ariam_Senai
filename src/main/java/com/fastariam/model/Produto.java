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

    private Double fatorQtdM3;

    private Double comprimento;
    private Double largura;
    private Double altura;

    private boolean ativo = true;

    public Produto() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String codigo, descricao, nomeAntigo;
        private TipoProduto tipo;
        private CategoriaLinhaSeca categoria;
        private Double fatorQtdM3, comprimento, largura, altura;
        private boolean ativo = true;

        public Builder codigo(String v) { this.codigo = v; return this; }
        public Builder descricao(String v) { this.descricao = v; return this; }
        public Builder nomeAntigo(String v) { this.nomeAntigo = v; return this; }
        public Builder tipo(TipoProduto v) { this.tipo = v; return this; }
        public Builder categoria(CategoriaLinhaSeca v) { this.categoria = v; return this; }
        public Builder fatorQtdM3(Double v) { this.fatorQtdM3 = v; return this; }
        public Builder comprimento(Double v) { this.comprimento = v; return this; }
        public Builder largura(Double v) { this.largura = v; return this; }
        public Builder altura(Double v) { this.altura = v; return this; }
        public Builder ativo(boolean v) { this.ativo = v; return this; }

        public Produto build() {
            Produto p = new Produto();
            p.codigo = codigo; p.descricao = descricao; p.nomeAntigo = nomeAntigo;
            p.tipo = tipo; p.categoria = categoria; p.fatorQtdM3 = fatorQtdM3;
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
    public Double getFatorQtdM3() { return fatorQtdM3; }
    public void setFatorQtdM3(Double v) { this.fatorQtdM3 = v; }
    public Double getComprimento() { return comprimento; }
    public void setComprimento(Double v) { this.comprimento = v; }
    public Double getLargura() { return largura; }
    public void setLargura(Double v) { this.largura = v; }
    public Double getAltura() { return altura; }
    public void setAltura(Double v) { this.altura = v; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean v) { this.ativo = v; }
}
