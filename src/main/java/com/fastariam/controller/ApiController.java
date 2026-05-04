package com.fastariam.controller;

import com.fastariam.model.Produto;
import com.fastariam.model.TipoProduto;
import com.fastariam.repository.CidadeRepository;
import com.fastariam.repository.ProdutoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ProdutoRepository produtoRepo;
    private final CidadeRepository cidadeRepo;

    public ApiController(ProdutoRepository produtoRepo, CidadeRepository cidadeRepo) {
        this.produtoRepo = produtoRepo; this.cidadeRepo = cidadeRepo;
    }

    @GetMapping("/produtos/buscar")
    public ResponseEntity<List<Produto>> buscarProdutos(@RequestParam String termo) {
        return ResponseEntity.ok(produtoRepo.buscarPorTermo(termo));
    }

    @GetMapping("/produtos")
    public ResponseEntity<List<Produto>> listarProdutos(@RequestParam(required = false) String tipo) {
        if (tipo != null) {
            return ResponseEntity.ok(produtoRepo.findByTipoAndAtivoTrue(TipoProduto.valueOf(tipo)));
        }
        return ResponseEntity.ok(produtoRepo.findByAtivoTrue());
    }

    @GetMapping("/cidades/buscar")
    public ResponseEntity<Object> buscarCidade(@RequestParam String nome,
                                                @RequestParam(required = false) String estado) {
        var cidades = cidadeRepo.findByNomeContainingIgnoreCase(nome);
        return ResponseEntity.ok(cidades);
    }

    @GetMapping("/cidades/estados")
    public ResponseEntity<List<String>> listarEstados() {
        return ResponseEntity.ok(cidadeRepo.findAllEstados());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "app", "Fast Ariam Logística"));
    }
}
