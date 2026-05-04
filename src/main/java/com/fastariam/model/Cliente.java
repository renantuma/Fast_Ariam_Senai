package com.fastariam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    @Column(nullable = false)
    private double fatorDescarga = 1.0;

    private boolean ativo = true;

    public Cliente() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String nome;
        private double fatorDescarga = 1.0;
        private boolean ativo = true;

        public Builder nome(String v) { this.nome = v; return this; }
        public Builder fatorDescarga(double v) { this.fatorDescarga = v; return this; }
        public Builder ativo(boolean v) { this.ativo = v; return this; }

        public Cliente build() {
            Cliente c = new Cliente();
            c.nome = nome; c.fatorDescarga = fatorDescarga; c.ativo = ativo;
            return c;
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String v) { this.nome = v; }
    public double getFatorDescarga() { return fatorDescarga; }
    public void setFatorDescarga(double v) { this.fatorDescarga = v; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean v) { this.ativo = v; }
}
