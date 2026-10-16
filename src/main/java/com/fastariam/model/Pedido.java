package com.fastariam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos", indexes = {
        @Index(name = "idx_pedidos_status", columnList = "status"),
        @Index(name = "idx_pedidos_criado_em", columnList = "criado_em"),
        @Index(name = "idx_pedidos_numero_pedido", columnList = "numero_pedido")
})
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String numeroPedido;

    @Column(length = 200)
    private String pdfOriginal;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "cidade_destino_id")
    private Cidade cidadeDestino;

    @Enumerated(EnumType.STRING)
    private TipoVeiculo veiculoSugerido;

    @Enumerated(EnumType.STRING)
    private TipoVeiculo veiculoConfirmado;

    @Column(name = "volume_total_m3")
    private double volumeTotalM3;

    /** Comprimento (m) de carroceria que a carga ocupa. */
    @Column(name = "comprimento_ocupado_m")
    private double comprimentoOcupadoM;

    /** Comprimento ocupado acrescido da margem NVIA (+10%). */
    @Column(name = "comprimento_margem_nvia_m")
    private double comprimentoMargemNviaM;

    /** Comprimento ocupado acrescido da margem de venda (+20%). */
    @Column(name = "comprimento_margem_venda_m")
    private double comprimentoMargemVendaM;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(length = 500)
    private String observacoes;

    @ManyToOne
    @JoinColumn(name = "criado_por_id")
    private Usuario criadoPor;

    private LocalDateTime criadoEm = LocalDateTime.now();
    private LocalDateTime atualizadoEm;

    public Pedido() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String numeroPedido, pdfOriginal, observacoes;
        private Cliente cliente;
        private Cidade cidadeDestino;
        private TipoVeiculo veiculoSugerido, veiculoConfirmado;
        private double volumeTotalM3, comprimentoOcupadoM, comprimentoMargemNviaM, comprimentoMargemVendaM;
        private StatusPedido status;
        private List<ItemPedido> itens = new ArrayList<>();
        private Usuario criadoPor;
        private LocalDateTime criadoEm;

        public Builder numeroPedido(String v) { this.numeroPedido = v; return this; }
        public Builder pdfOriginal(String v) { this.pdfOriginal = v; return this; }
        public Builder cliente(Cliente v) { this.cliente = v; return this; }
        public Builder cidadeDestino(Cidade v) { this.cidadeDestino = v; return this; }
        public Builder veiculoSugerido(TipoVeiculo v) { this.veiculoSugerido = v; return this; }
        public Builder veiculoConfirmado(TipoVeiculo v) { this.veiculoConfirmado = v; return this; }
        public Builder volumeTotalM3(double v) { this.volumeTotalM3 = v; return this; }
        public Builder comprimentoOcupadoM(double v) { this.comprimentoOcupadoM = v; return this; }
        public Builder comprimentoMargemNviaM(double v) { this.comprimentoMargemNviaM = v; return this; }
        public Builder comprimentoMargemVendaM(double v) { this.comprimentoMargemVendaM = v; return this; }
        public Builder status(StatusPedido v) { this.status = v; return this; }
        public Builder itens(List<ItemPedido> v) { this.itens = v; return this; }
        public Builder observacoes(String v) { this.observacoes = v; return this; }
        public Builder criadoPor(Usuario v) { this.criadoPor = v; return this; }
        public Builder criadoEm(LocalDateTime v) { this.criadoEm = v; return this; }

        public Pedido build() {
            Pedido p = new Pedido();
            p.numeroPedido = numeroPedido; p.pdfOriginal = pdfOriginal;
            p.cliente = cliente; p.cidadeDestino = cidadeDestino;
            p.veiculoSugerido = veiculoSugerido; p.veiculoConfirmado = veiculoConfirmado;
            p.volumeTotalM3 = volumeTotalM3; p.comprimentoOcupadoM = comprimentoOcupadoM;
            p.comprimentoMargemNviaM = comprimentoMargemNviaM; p.comprimentoMargemVendaM = comprimentoMargemVendaM;
            p.status = status; p.itens = itens != null ? itens : new ArrayList<>();
            p.observacoes = observacoes; p.criadoPor = criadoPor;
            p.criadoEm = criadoEm != null ? criadoEm : LocalDateTime.now();
            return p;
        }
    }

    @PreUpdate
    void preUpdate() { this.atualizadoEm = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String v) { this.numeroPedido = v; }
    public String getPdfOriginal() { return pdfOriginal; }
    public void setPdfOriginal(String v) { this.pdfOriginal = v; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente v) { this.cliente = v; }
    public Cidade getCidadeDestino() { return cidadeDestino; }
    public void setCidadeDestino(Cidade v) { this.cidadeDestino = v; }
    public TipoVeiculo getVeiculoSugerido() { return veiculoSugerido; }
    public void setVeiculoSugerido(TipoVeiculo v) { this.veiculoSugerido = v; }
    public TipoVeiculo getVeiculoConfirmado() { return veiculoConfirmado; }
    public void setVeiculoConfirmado(TipoVeiculo v) { this.veiculoConfirmado = v; }
    public double getVolumeTotalM3() { return volumeTotalM3; }
    public void setVolumeTotalM3(double v) { this.volumeTotalM3 = v; }
    public double getComprimentoOcupadoM() { return comprimentoOcupadoM; }
    public void setComprimentoOcupadoM(double v) { this.comprimentoOcupadoM = v; }
    public double getComprimentoMargemNviaM() { return comprimentoMargemNviaM; }
    public void setComprimentoMargemNviaM(double v) { this.comprimentoMargemNviaM = v; }
    public double getComprimentoMargemVendaM() { return comprimentoMargemVendaM; }
    public void setComprimentoMargemVendaM(double v) { this.comprimentoMargemVendaM = v; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido v) { this.status = v; }
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> v) { this.itens = v; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String v) { this.observacoes = v; }
    public Usuario getCriadoPor() { return criadoPor; }
    public void setCriadoPor(Usuario v) { this.criadoPor = v; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime v) { this.criadoEm = v; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
