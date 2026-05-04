package com.fastariam.dto;

public class ItemExtraidoDTO {
    private String codigoOriginal;
    private String descricaoOriginal;
    private int quantidade;
    private Long produtoId;
    private String produtoNome;
    private boolean identificado;

    public ItemExtraidoDTO() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String codigoOriginal, descricaoOriginal, produtoNome;
        private int quantidade;
        private Long produtoId;
        private boolean identificado;

        public Builder codigoOriginal(String v) { this.codigoOriginal = v; return this; }
        public Builder descricaoOriginal(String v) { this.descricaoOriginal = v; return this; }
        public Builder quantidade(int v) { this.quantidade = v; return this; }
        public Builder produtoId(Long v) { this.produtoId = v; return this; }
        public Builder produtoNome(String v) { this.produtoNome = v; return this; }
        public Builder identificado(boolean v) { this.identificado = v; return this; }

        public ItemExtraidoDTO build() {
            ItemExtraidoDTO d = new ItemExtraidoDTO();
            d.codigoOriginal = codigoOriginal; d.descricaoOriginal = descricaoOriginal;
            d.quantidade = quantidade; d.produtoId = produtoId;
            d.produtoNome = produtoNome; d.identificado = identificado;
            return d;
        }
    }

    public String getCodigoOriginal() { return codigoOriginal; }
    public void setCodigoOriginal(String v) { this.codigoOriginal = v; }
    public String getDescricaoOriginal() { return descricaoOriginal; }
    public void setDescricaoOriginal(String v) { this.descricaoOriginal = v; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int v) { this.quantidade = v; }
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long v) { this.produtoId = v; }
    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String v) { this.produtoNome = v; }
    public boolean isIdentificado() { return identificado; }
    public void setIdentificado(boolean v) { this.identificado = v; }
}
