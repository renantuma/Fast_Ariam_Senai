package com.fastariam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column
    private LocalDateTime ultimoAcesso;

    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Usuario() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String username, senha, nome;
        private PerfilUsuario perfil;
        private boolean ativo = true;
        private LocalDateTime ultimoAcesso, criadoEm;

        public Builder username(String v) { this.username = v; return this; }
        public Builder senha(String v) { this.senha = v; return this; }
        public Builder nome(String v) { this.nome = v; return this; }
        public Builder perfil(PerfilUsuario v) { this.perfil = v; return this; }
        public Builder ativo(boolean v) { this.ativo = v; return this; }
        public Builder ultimoAcesso(LocalDateTime v) { this.ultimoAcesso = v; return this; }
        public Builder criadoEm(LocalDateTime v) { this.criadoEm = v; return this; }

        public Usuario build() {
            Usuario u = new Usuario();
            u.username = username; u.senha = senha; u.nome = nome;
            u.perfil = perfil; u.ativo = ativo;
            u.ultimoAcesso = ultimoAcesso;
            u.criadoEm = criadoEm != null ? criadoEm : LocalDateTime.now();
            return u;
        }
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getSenha() { return senha; }
    public void setSenha(String v) { this.senha = v; }
    public String getNome() { return nome; }
    public void setNome(String v) { this.nome = v; }
    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario v) { this.perfil = v; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean v) { this.ativo = v; }
    public LocalDateTime getUltimoAcesso() { return ultimoAcesso; }
    public void setUltimoAcesso(LocalDateTime v) { this.ultimoAcesso = v; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime v) { this.criadoEm = v; }
}
