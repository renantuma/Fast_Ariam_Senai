package com.fastariam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(length = 100)
    private String codigoOriginal;

    @Column(length = 200)
    private String descricaoOriginal;

    private int quantidade;
    private double volumeM3;
    private boolean identificadoAutomaticamente;

    public ItemPedido() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Pedido pedido;
        private Produto produto;
        private String codigoOriginal, descricaoOriginal;
        private int quantidade;
        private double volumeM3;
        private boolean identificadoAutomaticamente;

        public Builder pedido(Pedido v) { this.pedido = v; return this; }
        public Builder produto(Produto v) { this.produto = v; return this; }
        public Builder codigoOriginal(String v) { this.codigoOriginal = v; return this; }
        public Builder descricaoOriginal(String v) { this.descricaoOriginal = v; return this; }
        public Builder quantidade(int v) { this.quantidade = v; return this; }
        public Builder volumeM3(double v) { this.volumeM3 = v; return this; }
        public Builder identificadoAutomaticamente(boolean v) { this.identificadoAutomaticamente = v; return this; }

        public ItemPedido build() {
            ItemPedido i = new ItemPedido();
            i.pedido = pedido; i.produto = produto;
            i.codigoOriginal = codigoOriginal; i.descricaoOriginal = descricaoOriginal;
            i.quantidade = quantidade; i.volumeM3 = volumeM3;
            i.identificadoAutomaticamente = identificadoAutomaticamente;
            return i;
        }
    }

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido v) { this.pedido = v; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto v) { this.produto = v; }
    public String getCodigoOriginal() { return codigoOriginal; }
    public void setCodigoOriginal(String v) { this.codigoOriginal = v; }
    public String getDescricaoOriginal() { return descricaoOriginal; }
    public void setDescricaoOriginal(String v) { this.descricaoOriginal = v; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int v) { this.quantidade = v; }
    public double getVolumeM3() { return volumeM3; }
    public void setVolumeM3(double v) { this.volumeM3 = v; }
    public boolean isIdentificadoAutomaticamente() { return identificadoAutomaticamente; }
    public void setIdentificadoAutomaticamente(boolean v) { this.identificadoAutomaticamente = v; }
}
