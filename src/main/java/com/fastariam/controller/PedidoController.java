package com.fastariam.controller;

import com.fastariam.dto.ItemExtraidoDTO;
import com.fastariam.dto.ResultadoFreteDTO;
import com.fastariam.dto.ResultadoVolumetriaDTO;
import com.fastariam.model.*;
import com.fastariam.repository.*;
import com.fastariam.service.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final VolumetriaService volumetriaService;
    private final FreteService freteService;
    private final PedidoRepository pedidoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ProdutoRepository produtoRepo;
    private final ClienteRepository clienteRepo;
    private final CidadeRepository cidadeRepo;
    private final PropostaRepository propostaRepo;

    public PedidoController(PedidoService pedidoService, VolumetriaService volumetriaService,
                             FreteService freteService, PedidoRepository pedidoRepo,
                             UsuarioRepository usuarioRepo, ProdutoRepository produtoRepo,
                             ClienteRepository clienteRepo, CidadeRepository cidadeRepo,
                             PropostaRepository propostaRepo) {
        this.pedidoService = pedidoService;
        this.volumetriaService = volumetriaService;
        this.freteService = freteService;
        this.pedidoRepo = pedidoRepo;
        this.usuarioRepo = usuarioRepo;
        this.produtoRepo = produtoRepo;
        this.clienteRepo = clienteRepo;
        this.cidadeRepo = cidadeRepo;
        this.propostaRepo = propostaRepo;
    }

    @GetMapping
    public String listar(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("pedidos", pedidoService.listar(PageRequest.of(page, 15)));
        return "pedidos/lista";
    }

    @GetMapping("/novo")
    public String novo() {
        return "pedidos/novo";
    }

    @PostMapping("/upload-pdf")
    public String uploadPdf(@RequestParam("arquivo") MultipartFile arquivo,
                             @AuthenticationPrincipal UserDetails ud,
                             RedirectAttributes ra) throws IOException {
        Usuario usuario = usuarioRepo.findByUsername(ud.getUsername()).orElseThrow();
        List<ItemExtraidoDTO> itens = pedidoService.uploadPedidoPdf(arquivo, usuario);
        ra.addFlashAttribute("itensExtraidos", itens);
        ra.addFlashAttribute("nomeArquivo", arquivo.getOriginalFilename());
        return "redirect:/pedidos/revisar-itens";
    }

    @GetMapping("/revisar-itens")
    public String revisarItens(Model model) {
        if (!model.containsAttribute("itensExtraidos")) {
            return "redirect:/pedidos/novo";
        }
        model.addAttribute("produtos", produtoRepo.findByAtivoTrue());
        return "pedidos/revisar-itens";
    }

    @PostMapping("/calcular-volumetria")
    public String calcularVolumetria(@RequestParam String numeroPedido,
                                      @RequestParam List<String> codigos,
                                      @RequestParam List<String> descricoes,
                                      @RequestParam List<Integer> quantidades,
                                      @RequestParam List<Long> produtoIds,
                                      @AuthenticationPrincipal UserDetails ud,
                                      RedirectAttributes ra) {

        Usuario usuario = usuarioRepo.findByUsername(ud.getUsername()).orElseThrow();

        List<ItemExtraidoDTO> itens = new java.util.ArrayList<>();
        for (int i = 0; i < codigos.size(); i++) {
            Long pid = produtoIds.get(i);
            itens.add(ItemExtraidoDTO.builder()
                    .codigoOriginal(codigos.get(i))
                    .descricaoOriginal(descricoes.get(i))
                    .quantidade(quantidades.get(i))
                    .produtoId(pid > 0 ? pid : null)
                    .identificado(pid > 0)
                    .build());
        }

        Pedido pedido = pedidoService.criarPedido(numeroPedido, itens, usuario);
        ra.addFlashAttribute("pedidoId", pedido.getId());
        ra.addFlashAttribute("volumetria", calcularVolumetriaDto(pedido));
        return "redirect:/pedidos/" + pedido.getId() + "/frete";
    }

    @GetMapping("/{id}/frete")
    public String freteForm(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.buscar(id);
        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteRepo.findByAtivoTrueOrderByNome());
        model.addAttribute("cidades", cidadeRepo.findAll());
        model.addAttribute("estados", cidadeRepo.findAllEstados());
        return "pedidos/frete";
    }

    @PostMapping("/{id}/calcular-frete")
    public String calcularFrete(@PathVariable Long id,
                                 @RequestParam String nomeCidade,
                                 @RequestParam String estado,
                                 @RequestParam(defaultValue = "0") Long clienteId,
                                 @RequestParam(defaultValue = "0") double valorMercadoria,
                                 RedirectAttributes ra) {
        ResultadoFreteDTO frete = pedidoService.calcularFrete(id, nomeCidade, estado, clienteId, valorMercadoria);
        ra.addFlashAttribute("frete", frete);
        return "redirect:/pedidos/" + id + "/proposta";
    }

    @GetMapping("/{id}/proposta")
    public String propostaForm(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.buscar(id);
        model.addAttribute("pedido", pedido);
        model.addAttribute("tiposVeiculo", TipoVeiculo.values());
        return "pedidos/proposta";
    }

    @PostMapping("/{id}/gerar-proposta")
    public String gerarProposta(@PathVariable Long id,
                                 @RequestParam String veiculoEscolhido,
                                 @RequestParam(defaultValue = "0") double ajusteManual,
                                 @RequestParam(required = false) String justificativa,
                                 @RequestParam String nomeCidade,
                                 @RequestParam String estado,
                                 @RequestParam(defaultValue = "0") Long clienteId,
                                 @RequestParam(defaultValue = "0") double valorMercadoria,
                                 @AuthenticationPrincipal UserDetails ud,
                                 RedirectAttributes ra) throws IOException {

        Usuario usuario = usuarioRepo.findByUsername(ud.getUsername()).orElseThrow();
        TipoVeiculo veiculo = TipoVeiculo.valueOf(veiculoEscolhido);

        double fatorDescarga = 1.0;
        if (clienteId > 0) {
            fatorDescarga = clienteRepo.findById(clienteId)
                    .map(Cliente::getFatorDescarga).orElse(1.0);
        }

        Pedido pedido = pedidoService.buscar(id);
        ResultadoFreteDTO frete = freteService.calcular(nomeCidade, estado, valorMercadoria,
                fatorDescarga, pedido.getVolumeTotalM3());

        String caminhoPdf = pedidoService.gerarProposta(id, frete, veiculo, ajusteManual, justificativa, usuario);

        ra.addFlashAttribute("sucesso", "Proposta gerada com sucesso!");
        ra.addFlashAttribute("arquivoPdf", caminhoPdf);
        return "redirect:/pedidos/" + id + "/sucesso";
    }

    @GetMapping("/{id}/sucesso")
    public String sucesso(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", pedidoService.buscar(id));
        return "pedidos/sucesso";
    }

    @GetMapping("/{id}/download-proposta")
    public ResponseEntity<Resource> downloadProposta(@PathVariable Long id) {
        pedidoService.buscar(id); // valida existência
        File dir = new File("./uploads/propostas");
        File[] arquivos = dir.listFiles((d, name) -> name.startsWith("Proposta_" + id + "_"));
        if (arquivos == null || arquivos.length == 0) {
            return ResponseEntity.notFound().build();
        }
        File arquivo = arquivos[arquivos.length - 1];
        Resource resource = new FileSystemResource(arquivo);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + arquivo.getName() + "\"")
                .body(resource);
    }

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(PedidoController.class.getName());

    @PostMapping("/{id}/excluir")
    public String excluirViaPost(@PathVariable Long id, RedirectAttributes ra) {
        try {
            pedidoService.excluir(id);
            ra.addFlashAttribute("sucesso", "Pedido #" + id + " excluído com sucesso.");
            log.info("Pedido " + id + " excluído com sucesso.");
        } catch (IllegalArgumentException e) {
            log.warning("Tentativa de excluir pedido inexistente: " + id);
            ra.addFlashAttribute("erro", "Pedido #" + id + " não encontrado.");
        } catch (Exception e) {
            log.severe("Erro ao excluir pedido " + id + ": " + e.getMessage());
            ra.addFlashAttribute("erro", "Não foi possível excluir o pedido #" + id + ". " + e.getMessage());
        }
        return "redirect:/pedidos";
    }


    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", pedidoService.buscar(id));
        return "pedidos/detalhe";
    }

    private ResultadoVolumetriaDTO calcularVolumetriaDto(Pedido pedido) {
        return volumetriaService.calcular(pedido.getItens());
    }
}
