package com.fastariam.controller;

import com.fastariam.model.*;
import com.fastariam.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioRepository usuarioRepo;
    private final ProdutoRepository produtoRepo;
    private final CidadeRepository cidadeRepo;
    private final ClienteRepository clienteRepo;
    private final ConfiguracaoFreteRepository configRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UsuarioRepository usuarioRepo, ProdutoRepository produtoRepo,
                            CidadeRepository cidadeRepo, ClienteRepository clienteRepo,
                            ConfiguracaoFreteRepository configRepo, PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo; this.produtoRepo = produtoRepo;
        this.cidadeRepo = cidadeRepo; this.clienteRepo = clienteRepo;
        this.configRepo = configRepo; this.passwordEncoder = passwordEncoder;
    }

    // ---- Usuários ----
    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepo.findAll());
        return "admin/usuarios";
    }

    @PostMapping("/usuarios/salvar")
    public String salvarUsuario(@RequestParam(required = false) Long id,
                                 @RequestParam String username,
                                 @RequestParam String nome,
                                 @RequestParam String perfil,
                                 @RequestParam(required = false) String senha,
                                 RedirectAttributes ra) {
        Usuario u;
        if (id != null) {
            u = usuarioRepo.findById(id).orElseThrow();
        } else {
            u = new Usuario();
            u.setAtivo(true);
        }
        u.setUsername(username);
        u.setNome(nome);
        u.setPerfil(PerfilUsuario.valueOf(perfil));
        if (senha != null && !senha.isBlank()) {
            u.setSenha(passwordEncoder.encode(senha));
        }
        usuarioRepo.save(u);
        ra.addFlashAttribute("sucesso", "Usuário salvo com sucesso!");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/toggle")
    public String toggleUsuario(@PathVariable Long id, RedirectAttributes ra) {
        Usuario u = usuarioRepo.findById(id).orElseThrow();
        u.setAtivo(!u.isAtivo());
        usuarioRepo.save(u);
        ra.addFlashAttribute("sucesso", "Status do usuário atualizado.");
        return "redirect:/admin/usuarios";
    }

    // ---- Produtos ----
    @GetMapping("/produtos")
    public String produtos(Model model,
                            @RequestParam(required = false) String tipo,
                            @RequestParam(required = false) String termo) {
        if (termo != null && !termo.isBlank()) {
            model.addAttribute("produtos", produtoRepo.buscarPorTermo(termo));
        } else if (tipo != null && !tipo.isBlank()) {
            model.addAttribute("produtos", produtoRepo.findByTipoAndAtivoTrue(TipoProduto.valueOf(tipo)));
        } else {
            model.addAttribute("produtos", produtoRepo.findByAtivoTrue());
        }
        model.addAttribute("categorias", CategoriaLinhaSeca.values());
        model.addAttribute("tipos", TipoProduto.values());
        return "admin/produtos";
    }

    @PostMapping("/produtos/salvar")
    public String salvarProduto(@RequestParam(required = false) Long id,
                                 @RequestParam String codigo,
                                 @RequestParam String descricao,
                                 @RequestParam(required = false) String nomeAntigo,
                                 @RequestParam String tipo,
                                 @RequestParam(required = false) String categoria,
                                 @RequestParam(required = false) Double fatorQtdM3,
                                 @RequestParam(required = false) Double comprimento,
                                 @RequestParam(required = false) Double largura,
                                 @RequestParam(required = false) Double altura,
                                 RedirectAttributes ra) {
        Produto p;
        if (id != null) {
            p = produtoRepo.findById(id).orElse(new Produto());
        } else {
            p = new Produto();
            p.setAtivo(true);
        }
        p.setCodigo(codigo);
        p.setDescricao(descricao);
        p.setNomeAntigo(nomeAntigo);
        p.setTipo(TipoProduto.valueOf(tipo));
        if (categoria != null && !categoria.isBlank()) p.setCategoria(CategoriaLinhaSeca.valueOf(categoria));
        p.setFatorQtdM3(fatorQtdM3);
        p.setComprimento(comprimento);
        p.setLargura(largura);
        p.setAltura(altura);
        produtoRepo.save(p);
        ra.addFlashAttribute("sucesso", "Produto salvo com sucesso!");
        return "redirect:/admin/produtos";
    }

    @PostMapping("/produtos/{id}/toggle")
    public String toggleProduto(@PathVariable Long id, RedirectAttributes ra) {
        Produto p = produtoRepo.findById(id).orElseThrow();
        p.setAtivo(!p.isAtivo());
        produtoRepo.save(p);
        ra.addFlashAttribute("sucesso", "Status do produto atualizado.");
        return "redirect:/admin/produtos";
    }

    // ---- Cidades ----
    @GetMapping("/cidades")
    public String cidades(Model model, @RequestParam(required = false) String estado) {
        if (estado != null && !estado.isBlank()) {
            model.addAttribute("cidades", cidadeRepo.findByEstadoOrderByNome(estado));
        } else {
            model.addAttribute("cidades", cidadeRepo.findAll());
        }
        model.addAttribute("estados", cidadeRepo.findAllEstados());
        return "admin/cidades";
    }

    @PostMapping("/cidades/salvar")
    public String salvarCidade(@RequestParam(required = false) Long id,
                                @RequestParam String nome,
                                @RequestParam String estado,
                                @RequestParam double distanciaKm,
                                @RequestParam double freteBaseKmTruck,
                                @RequestParam double freteBaseKmCarreta,
                                @RequestParam double pedagioTruck,
                                @RequestParam double pedagioCarreta,
                                @RequestParam double icmsPercent,
                                RedirectAttributes ra) {
        Cidade c;
        if (id != null) {
            c = cidadeRepo.findById(id).orElse(new Cidade());
        } else {
            c = new Cidade();
        }
        c.setNome(nome);
        c.setEstado(estado.toUpperCase());
        c.setDistanciaKm(distanciaKm);
        c.setFreteBaseKmTruck(freteBaseKmTruck);
        c.setFreteBaseKmCarreta(freteBaseKmCarreta);
        c.setPedagioTruck(pedagioTruck);
        c.setPedagioCarreta(pedagioCarreta);
        c.setIcmsPercent(icmsPercent);
        c.setViaApi(false);
        cidadeRepo.save(c);
        ra.addFlashAttribute("sucesso", "Cidade salva com sucesso!");
        return "redirect:/admin/cidades";
    }

    @PostMapping("/cidades/{id}/excluir")
    public String excluirCidade(@PathVariable Long id, RedirectAttributes ra) {
        cidadeRepo.deleteById(id);
        ra.addFlashAttribute("sucesso", "Cidade removida.");
        return "redirect:/admin/cidades";
    }

    // ---- Clientes ----
    @GetMapping("/clientes")
    public String clientes(Model model) {
        model.addAttribute("clientes", clienteRepo.findByAtivoTrueOrderByNome());
        return "admin/clientes";
    }

    @PostMapping("/clientes/salvar")
    public String salvarCliente(@RequestParam(required = false) Long id,
                                 @RequestParam String nome,
                                 @RequestParam double fatorDescarga,
                                 RedirectAttributes ra) {
        Cliente c;
        if (id != null) {
            c = clienteRepo.findById(id).orElse(new Cliente());
        } else {
            c = new Cliente();
            c.setAtivo(true);
        }
        c.setNome(nome);
        c.setFatorDescarga(fatorDescarga);
        clienteRepo.save(c);
        ra.addFlashAttribute("sucesso", "Cliente salvo com sucesso!");
        return "redirect:/admin/clientes";
    }

    // ---- Configuração de Frete ----
    @GetMapping("/configuracao")
    public String configuracao(Model model) {
        ConfiguracaoFrete config = configRepo.getConfiguracao();
        model.addAttribute("config", config != null ? config : new ConfiguracaoFrete());
        return "admin/configuracao";
    }

    @PostMapping("/configuracao/salvar")
    public String salvarConfiguracao(@RequestParam(required = false) Long id,
                                      @RequestParam double descargarEmpilhadeira,
                                      @RequestParam double descargarManualTruck,
                                      @RequestParam double descargarManualCarreta,
                                      @RequestParam double pisCofinsPercent,
                                      @RequestParam double margemComercialPercent,
                                      @RequestParam double adValoremDefault,
                                      @RequestParam double capacidadeCarretaM3,
                                      RedirectAttributes ra) {
        ConfiguracaoFrete c;
        if (id != null) {
            c = configRepo.findById(id).orElse(new ConfiguracaoFrete());
        } else {
            c = configRepo.getConfiguracao();
            if (c == null) c = new ConfiguracaoFrete();
        }
        c.setDescargarEmpilhadeira(descargarEmpilhadeira);
        c.setDescargarManualTruck(descargarManualTruck);
        c.setDescargarManualCarreta(descargarManualCarreta);
        c.setPisCofinsPercent(pisCofinsPercent);
        c.setMargemComercialPercent(margemComercialPercent);
        c.setAdValoremDefault(adValoremDefault);
        c.setCapacidadeCarretaM3(capacidadeCarretaM3);
        configRepo.save(c);
        ra.addFlashAttribute("sucesso", "Configuração salva com sucesso!");
        return "redirect:/admin/configuracao";
    }
}
