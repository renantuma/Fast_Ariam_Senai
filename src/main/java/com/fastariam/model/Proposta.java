package com.fastariam.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Proposta de frete gerada para um pedido.
 *
 * <p>Guarda só o que é comum à proposta inteira (pedido, valor da mercadoria,
 * alíquotas usadas, veículo escolhido, ajuste e PDF). Os valores calculados
 * para cada veículo ficam em {@link PropostaOpcao}: uma linha por veículo, em
 * vez de uma coluna por veículo. Assim, incluir um novo tipo de veículo não
 * exige alterar a estrutura da tabela.</p>
 *
 * <p>As alíquotas ({@code icmsPercent}, {@code pisCofinsPercent}, ...) são uma
 * cópia dos valores vigentes no momento da emissão, para que a proposta não mude
 * se a configuração global for alterada depois.</p>
 */
@Entity
@Table(name = "propostas")
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    // ---- Dados usados no cálculo (cópia do momento da emissão) ----
    @Column(name = "valor_mercadoria", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorMercadoria = BigDecimal.ZERO;

    @Column(name = "icms_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal icmsPercent = BigDecimal.ZERO;

    @Column(name = "pis_cofins_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal pisCofinsPercent = new BigDecimal("9.25");

    @Column(name = "margem_comercial_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal margemComercialPercent = new BigDecimal("20.00");

    @Column(name = "ad_valorem_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal adValoremPercent = BigDecimal.ZERO;

    // ---- Resultado ----
    @Enumerated(EnumType.STRING)
    @Column(name = "veiculo_escolhido", length = 20)
    private TipoVeiculo veiculoEscolhido;

    /** Valor do frete do veículo escolhido (sem o ajuste manual). */
    @Column(name = "frete_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal freteTotal = BigDecimal.ZERO;

    @Column(name = "ajuste_manual", nullable = false, precision = 12, scale = 2)
    private BigDecimal ajusteManual = BigDecimal.ZERO;

    @Column(name = "justificativa_ajuste", length = 300)
    private String justificativaAjuste;

    /** Caminho do PDF da proposta no disco. */
    @Column(name = "caminho_pdf", length = 300)
    private String caminhoPdf;

    @Column(name = "gerada_em", nullable = false)
    private LocalDateTime geradaEm = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "gerada_por_id")
    private Usuario geradaPor;

    /** Valores calculados por veículo (uma linha por veículo). */
    @OneToMany(mappedBy = "proposta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<PropostaOpcao> opcoes = new ArrayList<>();

    public Proposta() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Pedido pedido;
        private BigDecimal valorMercadoria = BigDecimal.ZERO;
        private BigDecimal icmsPercent = BigDecimal.ZERO;
        private BigDecimal pisCofinsPercent = new BigDecimal("9.25");
        private BigDecimal margemComercialPercent = new BigDecimal("20.00");
        private BigDecimal adValoremPercent = BigDecimal.ZERO;
        private TipoVeiculo veiculoEscolhido;
        private BigDecimal freteTotal = BigDecimal.ZERO;
        private BigDecimal ajusteManual = BigDecimal.ZERO;
        private String justificativaAjuste, caminhoPdf;
        private LocalDateTime geradaEm;
        private Usuario geradaPor;

        public Builder pedido(Pedido v) { this.pedido = v; return this; }
        public Builder valorMercadoria(BigDecimal v) { this.valorMercadoria = v; return this; }
        public Builder icmsPercent(BigDecimal v) { this.icmsPercent = v; return this; }
        public Builder pisCofinsPercent(BigDecimal v) { this.pisCofinsPercent = v; return this; }
        public Builder margemComercialPercent(BigDecimal v) { this.margemComercialPercent = v; return this; }
        public Builder adValoremPercent(BigDecimal v) { this.adValoremPercent = v; return this; }
        public Builder veiculoEscolhido(TipoVeiculo v) { this.veiculoEscolhido = v; return this; }
        public Builder freteTotal(BigDecimal v) { this.freteTotal = v; return this; }
        public Builder ajusteManual(BigDecimal v) { this.ajusteManual = v; return this; }
        public Builder justificativaAjuste(String v) { this.justificativaAjuste = v; return this; }
        public Builder caminhoPdf(String v) { this.caminhoPdf = v; return this; }
        public Builder geradaEm(LocalDateTime v) { this.geradaEm = v; return this; }
        public Builder geradaPor(Usuario v) { this.geradaPor = v; return this; }

        public Proposta build() {
            Proposta p = new Proposta();
            p.pedido = pedido;
            p.valorMercadoria = valorMercadoria;
            p.icmsPercent = icmsPercent;
            p.pisCofinsPercent = pisCofinsPercent;
            p.margemComercialPercent = margemComercialPercent;
            p.adValoremPercent = adValoremPercent;
            p.veiculoEscolhido = veiculoEscolhido;
            p.freteTotal = freteTotal;
            p.ajusteManual = ajusteManual;
            p.justificativaAjuste = justificativaAjuste;
            p.caminhoPdf = caminhoPdf;
            p.geradaEm = geradaEm != null ? geradaEm : LocalDateTime.now();
            p.geradaPor = geradaPor;
            return p;
        }
    }

    /** Adiciona uma opção de veículo e mantém os dois lados da relação coerentes. */
    public void adicionarOpcao(PropostaOpcao opcao) {
        opcao.setProposta(this);
        this.opcoes.add(opcao);
    }

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido v) { this.pedido = v; }
    public BigDecimal getValorMercadoria() { return valorMercadoria; }
    public void setValorMercadoria(BigDecimal v) { this.valorMercadoria = v; }
    public BigDecimal getIcmsPercent() { return icmsPercent; }
    public void setIcmsPercent(BigDecimal v) { this.icmsPercent = v; }
    public BigDecimal getPisCofinsPercent() { return pisCofinsPercent; }
    public void setPisCofinsPercent(BigDecimal v) { this.pisCofinsPercent = v; }
    public BigDecimal getMargemComercialPercent() { return margemComercialPercent; }
    public void setMargemComercialPercent(BigDecimal v) { this.margemComercialPercent = v; }
    public BigDecimal getAdValoremPercent() { return adValoremPercent; }
    public void setAdValoremPercent(BigDecimal v) { this.adValoremPercent = v; }
    public TipoVeiculo getVeiculoEscolhido() { return veiculoEscolhido; }
    public void setVeiculoEscolhido(TipoVeiculo v) { this.veiculoEscolhido = v; }
    public BigDecimal getFreteTotal() { return freteTotal; }
    public void setFreteTotal(BigDecimal v) { this.freteTotal = v; }
    public BigDecimal getAjusteManual() { return ajusteManual; }
    public void setAjusteManual(BigDecimal v) { this.ajusteManual = v; }
    public String getJustificativaAjuste() { return justificativaAjuste; }
    public void setJustificativaAjuste(String v) { this.justificativaAjuste = v; }
    public String getCaminhoPdf() { return caminhoPdf; }
    public void setCaminhoPdf(String v) { this.caminhoPdf = v; }
    public LocalDateTime getGeradaEm() { return geradaEm; }
    public void setGeradaEm(LocalDateTime v) { this.geradaEm = v; }
    public Usuario getGeradaPor() { return geradaPor; }
    public void setGeradaPor(Usuario v) { this.geradaPor = v; }
    public List<PropostaOpcao> getOpcoes() { return opcoes; }
}
