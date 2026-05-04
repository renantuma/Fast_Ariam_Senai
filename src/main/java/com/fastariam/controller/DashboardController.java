package com.fastariam.controller;

import com.fastariam.model.StatusPedido;
import com.fastariam.repository.PedidoRepository;
import com.fastariam.repository.ProdutoRepository;
import com.fastariam.repository.CidadeRepository;
import com.fastariam.repository.ClienteRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final PedidoRepository pedidoRepo;
    private final ProdutoRepository produtoRepo;
    private final CidadeRepository cidadeRepo;
    private final ClienteRepository clienteRepo;

    public DashboardController(PedidoRepository pedidoRepo, ProdutoRepository produtoRepo,
                                CidadeRepository cidadeRepo, ClienteRepository clienteRepo) {
        this.pedidoRepo = pedidoRepo; this.produtoRepo = produtoRepo;
        this.cidadeRepo = cidadeRepo; this.clienteRepo = clienteRepo;
    }

    @GetMapping("/")
    public String root() { return "redirect:/dashboard"; }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalPedidos", pedidoRepo.count());
        model.addAttribute("pedidosHoje", pedidoRepo.count()); // simplificado
        model.addAttribute("propostasGeradas",
                pedidoRepo.findAll().stream()
                        .filter(p -> p.getStatus() == StatusPedido.PROPOSTA_GERADA).count());
        model.addAttribute("totalProdutos", produtoRepo.count());
        model.addAttribute("totalCidades", cidadeRepo.count());
        model.addAttribute("pedidosRecentes",
                pedidoRepo.findAllByOrderByCriadoEmDesc(PageRequest.of(0, 5)).getContent());
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() { return "login"; }
}
