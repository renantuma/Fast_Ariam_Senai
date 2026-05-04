package com.fastariam.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastariam.dto.ItemExtraidoDTO;
import com.fastariam.dto.ResultadoFreteDTO;
import com.fastariam.dto.ResultadoVolumetriaDTO;
import com.fastariam.model.*;
import com.fastariam.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PedidoService {
    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);


    private final PedidoRepository pedidoRepo;
    private final ProdutoRepository produtoRepo;
    private final CidadeRepository cidadeRepo;
    private final ClienteRepository clienteRepo;
    private final PropostaRepository propostaRepo;
    private final PdfExtratorService pdfExtrator;
    private final VolumetriaService volumetriaService;
    private final FreteService freteService;
    private final PropostaGeracaoService propostaGeracao;

    public PedidoService(PedidoRepository pedidoRepo, ProdutoRepository produtoRepo,
                          CidadeRepository cidadeRepo, ClienteRepository clienteRepo,
                          PropostaRepository propostaRepo, PdfExtratorService pdfExtrator,
                          VolumetriaService volumetriaService, FreteService freteService,
                          PropostaGeracaoService propostaGeracao) {
        this.pedidoRepo = pedidoRepo; this.produtoRepo = produtoRepo;
        this.cidadeRepo = cidadeRepo; this.clienteRepo = clienteRepo;
        this.propostaRepo = propostaRepo; this.pdfExtrator = pdfExtrator;
        this.volumetriaService = volumetriaService; this.freteService = freteService;
        this.propostaGeracao = propostaGeracao;
    }

    /**
     * RF-01/02: Upload e extração de PDF.
     */
    public List<ItemExtraidoDTO> uploadPedidoPdf(MultipartFile arquivo, Usuario usuario) throws IOException {
        return pdfExtrator.extrair(arquivo);
    }

    /**
     * Cria um pedido a partir dos itens revisados pelo operador.
     */
    public Pedido criarPedido(String numeroPedido, List<ItemExtraidoDTO> itensRevisados, Usuario usuario) {
        Pedido pedido = Pedido.builder()
                .numeroPedido(numeroPedido)
                .status(StatusPedido.EM_CALCULO)
                .criadoPor(usuario)
                .criadoEm(LocalDateTime.now())
                .itens(new ArrayList<>())
                .build();

        pedido = pedidoRepo.save(pedido);

        for (ItemExtraidoDTO dto : itensRevisados) {
            Produto produto = null;
            if (dto.getProdutoId() != null) {
                produto = produtoRepo.findById(dto.getProdutoId()).orElse(null);
            }

            ItemPedido item = ItemPedido.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .codigoOriginal(dto.getCodigoOriginal())
                    .descricaoOriginal(dto.getDescricaoOriginal())
                    .quantidade(dto.getQuantidade())
                    .identificadoAutomaticamente(dto.isIdentificado())
                    .build();

            pedido.getItens().add(item);
        }

        // Calcular volumetria imediatamente
        ResultadoVolumetriaDTO vol = volumetriaService.calcular(pedido.getItens());
        pedido.setVolumeTotalM3(vol.getVolumeTotalM3());
        pedido.setMetrosCarroceria(vol.getMetrosCarroceria());
        pedido.setMetrosComMargemNvia(vol.getMetrosComMargemNvia());
        pedido.setMetrosComMargemVenda(vol.getMetrosComMargemVenda());
        pedido.setVeiculoSugerido(vol.getVeiculoSugerido());

        // Atualizar volume por item
        atualizarVolumeItens(pedido.getItens(), vol);

        return pedidoRepo.save(pedido);
    }

    /**
     * Define destino e calcula frete.
     */
    public ResultadoFreteDTO calcularFrete(Long pedidoId, String nomeCidade, String estado,
                                            Long clienteId, double valorMercadoria) {
        Pedido pedido = pedidoRepo.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        Cidade cidade = cidadeRepo.findByNomeAndEstado(nomeCidade, estado)
                .orElse(null);
        if (cidade != null) pedido.setCidadeDestino(cidade);

        double fatorDescarga = 1.0;
        if (clienteId != null) {
            Cliente cliente = clienteRepo.findById(clienteId).orElse(null);
            if (cliente != null) {
                pedido.setCliente(cliente);
                fatorDescarga = cliente.getFatorDescarga();
            }
        }

        pedidoRepo.save(pedido);

        return freteService.calcular(nomeCidade, estado, valorMercadoria, fatorDescarga, pedido.getVolumeTotalM3());
    }

    /**
     * Gera a proposta comercial em PDF.
     */
    public String gerarProposta(Long pedidoId, ResultadoFreteDTO frete, TipoVeiculo veiculoEscolhido,
                                 double ajusteManual, String justificativa, Usuario usuario) throws IOException {

        Pedido pedido = pedidoRepo.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        pedido.setVeiculoConfirmado(veiculoEscolhido);
        pedido.setStatus(StatusPedido.PROPOSTA_GERADA);

        Proposta proposta = Proposta.builder()
                .pedido(pedido)
                .freteBaseTruck(frete.getFreteBaseTruck())
                .freteBaseCarreta(frete.getFreteBaseCarreta())
                .pedagioTruck(frete.getPedagioTruck())
                .pedagioCarreta(frete.getPedagioCarreta())
                .descargarManualTruck(frete.getDescargarTruck())
                .descargarManualCarreta(frete.getDescargarCarreta())
                .adValorem(frete.getAdValoremTruck())
                .icmsPercent(frete.getIcmsPercent())
                .pisCofinsPercent(frete.getPisCofinsPercent())
                .margemComercial(frete.getMargemComercialPercent())
                .totalFreteTruck(frete.getTotalTruck())
                .totalFreteCarreta(frete.getTotalCarreta())
                .veiculoEscolhido(veiculoEscolhido)
                .freteTotal(veiculoEscolhido == TipoVeiculo.CARRETA ? frete.getTotalCarreta() : frete.getTotalTruck())
                .ajusteManual(ajusteManual)
                .justificativaAjuste(justificativa)
                .geradaEm(LocalDateTime.now())
                .geradaPor(usuario)
                .build();

        propostaRepo.save(proposta);
        pedidoRepo.save(pedido);

        return propostaGeracao.gerarPdf(pedido, proposta);
    }

    public Page<Pedido> listar(Pageable pageable) {
        return pedidoRepo.findAllByOrderByCriadoEmDesc(pageable);
    }

    public Pedido buscar(Long id) {
        return pedidoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));
    }

    private void atualizarVolumeItens(List<ItemPedido> itens,
                                       ResultadoVolumetriaDTO vol) {
        // Mapear volume por código
        for (ItemPedido item : itens) {
            if (item.getProduto() == null) continue;
            String cod = item.getProduto().getCodigo();

            vol.getItensRefrigerado().stream()
                    .filter(i -> cod.equals(i.getCodigo()))
                    .findFirst()
                    .ifPresent(i -> item.setVolumeM3(i.getVolumeAjustado()));

            vol.getItensSeca().stream()
                    .filter(i -> cod.equals(i.getCodigo()))
                    .findFirst()
                    .ifPresent(i -> item.setVolumeM3(i.getVolumeAjustado()));
        }
    }
}
