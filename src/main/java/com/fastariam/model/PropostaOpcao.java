package com.fastariam.model;

import com.fastariam.dto.OpcaoFreteDTO;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Valores de frete calculados para <b>um</b> tipo de veículo dentro de uma
 * {@link Proposta}. Todos os valores são em reais (R$).
 *
 * <p>Composição: {@code subtotal = freteBase + pedagio + custoDescarga + adValorem};
 * {@code impostos = subtotal × (ICMS + PIS/COFINS)};
 * {@code margemComercialValor = (subtotal + impostos) × margem%};
 * {@code total = subtotal + impostos + margemComercialValor}.</p>
 */
@Entity
@Table(
    name = "proposta_opcoes",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_proposta_opcoes_proposta_veiculo",
        columnNames = {"proposta_id", "tipo_veiculo"})
)
public class PropostaOpcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "proposta_id", nullable = false)
    private Proposta proposta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_veiculo", nullable = false, length = 20)
    private TipoVeiculo tipoVeiculo;

    @Column(name = "frete_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal freteBase = BigDecimal.ZERO;

    @Column(name = "pedagio", nullable = false, precision = 12, scale = 2)
    private BigDecimal pedagio = BigDecimal.ZERO;

    @Column(name = "custo_descarga", nullable = false, precision = 12, scale = 2)
    private BigDecimal custoDescarga = BigDecimal.ZERO;

    @Column(name = "ad_valorem", nullable = false, precision = 12, scale = 2)
    private BigDecimal adValorem = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "impostos", nullable = false, precision = 12, scale = 2)
    private BigDecimal impostos = BigDecimal.ZERO;

    @Column(name = "margem_comercial_valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal margemComercialValor = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    public PropostaOpcao() {}

    /** Cria a opção persistível a partir do resultado do cálculo de frete. */
    public static PropostaOpcao de(TipoVeiculo tipoVeiculo, OpcaoFreteDTO dto) {
        PropostaOpcao o = new PropostaOpcao();
        o.tipoVeiculo = tipoVeiculo;
        o.freteBase = dto.getFreteBase();
        o.pedagio = dto.getPedagio();
        o.custoDescarga = dto.getCustoDescarga();
        o.adValorem = dto.getAdValorem();
        o.subtotal = dto.getSubtotal();
        o.impostos = dto.getImpostos();
        o.margemComercialValor = dto.getMargemComercialValor();
        o.total = dto.getTotal();
        return o;
    }

    public Long getId() { return id; }
    public Proposta getProposta() { return proposta; }
    public void setProposta(Proposta v) { this.proposta = v; }
    public TipoVeiculo getTipoVeiculo() { return tipoVeiculo; }
    public void setTipoVeiculo(TipoVeiculo v) { this.tipoVeiculo = v; }
    public BigDecimal getFreteBase() { return freteBase; }
    public BigDecimal getPedagio() { return pedagio; }
    public BigDecimal getCustoDescarga() { return custoDescarga; }
    public BigDecimal getAdValorem() { return adValorem; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getImpostos() { return impostos; }
    public BigDecimal getMargemComercialValor() { return margemComercialValor; }
    public BigDecimal getTotal() { return total; }
}
