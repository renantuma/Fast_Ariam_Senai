package com.fastariam.config;

import com.fastariam.model.*;
import com.fastariam.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.logging.Logger;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = Logger.getLogger(DataInitializer.class.getName());
    private final UsuarioRepository usuarioRepo;
    private final ProdutoRepository produtoRepo;
    private final ClienteRepository clienteRepo;
    private final CidadeRepository cidadeRepo;
    private final ConfiguracaoFreteRepository configRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepo, ProdutoRepository produtoRepo,
                            ClienteRepository clienteRepo, CidadeRepository cidadeRepo,
                            ConfiguracaoFreteRepository configRepo, PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo; this.produtoRepo = produtoRepo;
        this.clienteRepo = clienteRepo; this.cidadeRepo = cidadeRepo;
        this.configRepo = configRepo; this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initUsuarios(); initConfiguracao(); initProdutosLinhaSeca();
        initProdutosRefrigerados(); initClientes(); initCidades();
        log.info("=== Fast Ariam inicializado! Acesse http://localhost:8080 ===");
        log.info("=== Login: admin / admin123 (ou operador / op123) ===");
    }

    private void initUsuarios() {
        if (usuarioRepo.count() > 0) return;
        usuarioRepo.save(Usuario.builder().username("admin").nome("Administrador")
                .senha(passwordEncoder.encode("admin123"))
                .perfil(PerfilUsuario.ADMINISTRADOR).ativo(true).build());
        usuarioRepo.save(Usuario.builder().username("operador").nome("Operador Logística")
                .senha(passwordEncoder.encode("op123"))
                .perfil(PerfilUsuario.OPERADOR).ativo(true).build());
        log.info("Usuários criados");
    }

    private void initConfiguracao() {
        if (configRepo.count() > 0) return;
        configRepo.save(ConfiguracaoFrete.builder()
                .descargarEmpilhadeira(350.00).descargarManualTruck(280.00)
                .descargarManualCarreta(420.00).pisCofinsPercent(9.25)
                .margemComercialPercent(20.0).adValoremDefault(0.3)
                .capacidadeCarretaM3(60.0).capacidadeContainerM3(45.0)
                .fatorMetrosCarroceria(12.0).build());
    }

    private void initProdutosLinhaSeca() {
        if (produtoRepo.findByTipoAndAtivoTrue(TipoProduto.LINHA_SECA).size() > 5) return;
        List<Produto> produtos = List.of(
            p("LSG-BASE","Gondola Base 1,00m","Base Gondola 1m",TipoProduto.LINHA_SECA,CategoriaLinhaSeca.GONDOLAS,8.0),
            p("LSG-GOND12","Gondola 1,25m Dupla Face","Gondola 1,25 DF",TipoProduto.LINHA_SECA,CategoriaLinhaSeca.GONDOLAS,6.0),
            p("LSG-GOND15","Gondola 1,50m Dupla Face","Gondola 1,50 DF",TipoProduto.LINHA_SECA,CategoriaLinhaSeca.GONDOLAS,5.0),
            p("LSG-PRATEL","Prateleira 1,00m","Prateleira 1m",TipoProduto.LINHA_SECA,CategoriaLinhaSeca.GONDOLAS,12.0),
            p("MOB-CAIXA","Caixa Checkout","Checkout Caixa",TipoProduto.LINHA_SECA,CategoriaLinhaSeca.MOBILIARIOS,2.0),
            p("MOB-BALCAO","Balcão Expositor",null,TipoProduto.LINHA_SECA,CategoriaLinhaSeca.MOBILIARIOS,1.5),
            p("RSL-1M","Rack Slim 1,00m",null,TipoProduto.LINHA_SECA,CategoriaLinhaSeca.RACK_SLIM,4.0),
            p("RSL-125","Rack Slim 1,25m",null,TipoProduto.LINHA_SECA,CategoriaLinhaSeca.RACK_SLIM,3.5),
            p("CHK-STD","Checkout Padrão","Check-out Padrão",TipoProduto.LINHA_SECA,CategoriaLinhaSeca.CHECKOUTS,1.0),
            p("PP-MONT-1","Porta Pallet Montado","Porta Pallet Montado",TipoProduto.LINHA_SECA,CategoriaLinhaSeca.PORTA_PALLETS_MONTADOS,2.0),
            p("PP-DESM-1","Porta Pallet Desmontado","Porta Pallet Desmontado",TipoProduto.LINHA_SECA,CategoriaLinhaSeca.PORTA_PALLETS_DESMONTADOS,0.5)
        );
        produtoRepo.saveAll(produtos);
        log.info(produtos.size() + " produtos linha seca cadastrados");
    }

    private void initProdutosRefrigerados() {
        if (produtoRepo.findByTipoAndAtivoTrue(TipoProduto.REFRIGERADO).size() > 5) return;
        List<Produto> produtos = List.of(
            r("FCA-4-100","FCA-4 1,00M (Expositor Curvo Aberto -4C)",1.56,1.20,1.72),
            r("FCF-0-187","FCF-0 1,87M (Expositor Curvo Fechado 0C)",2.30,1.20,1.72),
            r("FVP-0-180","FVP-0 1,80M (Expositor Vertical Porta 0C)",2.00,1.00,2.32),
            r("F01VD-0-120","F01 VD-0 1,20M (Dry Aged)",1.53,1.16,2.48),
            r("F01VPC-18","F01 VPC-18 2,10 (Plug-In Comb. -18C)",2.48,1.16,2.59),
            r("FCA-4-125","FCA-4 1,25M (Expositor Curvo Aberto -4C)",1.80,1.20,1.72),
            r("FCF-0-125","FCF-0 1,25M (Expositor Curvo Fechado 0C)",1.80,1.20,1.72),
            r("FIL-18-250","FIL-18 2,50M (Ilha Congelados -18C)",2.70,1.30,0.90),
            r("FIL-18-125","FIL-18 1,25M (Ilha Congelados -18C)",1.50,1.30,0.90),
            r("FVA-4-060","FVA-4 0,60M (Vertical Aberto -4C)",0.75,0.80,2.20),
            r("FVF-0-090","FVF-0 0,90M (Vertical Fechado 0C)",1.05,0.80,2.20),
            r("FCO-18-250","FCO-18 2,50M (Coffin Congelados -18C)",2.70,0.90,0.95),
            r("FBA-0-100","FBA-0 1,00M (Balcão Refrigerado 0C)",1.15,0.80,1.30),
            r("FBA-0-150","FBA-0 1,50M (Balcão Refrigerado 0C)",1.65,0.80,1.30),
            r("FMU-18-100","FMU-18 1,00M (Multideck -18C)",1.15,0.90,2.10)
        );
        produtoRepo.saveAll(produtos);
        log.info(produtos.size() + " produtos refrigerados cadastrados");
    }

    private void initClientes() {
        if (clienteRepo.count() > 0) return;
        List<Cliente> clientes = List.of(
            c("Outros Clientes",1.0), c("BIG",1.5), c("Bom Preço",1.5), c("BRF",1.5),
            c("Carrefour",1.5), c("CIA Brasileira",1.5), c("Cobasi",1.5),
            c("Dia Brasil",1.5), c("Sams Club",1.5), c("Tauste",1.5),
            c("Walmart",1.5), c("WMS",1.5), c("Armarinhos Fernando",1.5),
            c("Mamae Presentes",2.0), c("Atakarejo",2.0), c("Koch",2.0)
        );
        clienteRepo.saveAll(clientes);
        log.info(clientes.size() + " clientes cadastrados");
    }

    private void initCidades() {
        if (cidadeRepo.count() > 0) return;
        List<Cidade> cidades = List.of(
            cidade("São Paulo","SP",430,4.80,6.20,180,280,12.0),
            cidade("Campinas","SP",480,4.80,6.20,160,250,12.0),
            cidade("Ribeirão Preto","SP",290,4.50,5.80,120,180,12.0),
            cidade("Santos","SP",520,5.20,6.80,200,320,12.0),
            cidade("Sorocaba","SP",460,4.80,6.20,170,260,12.0),
            cidade("Curitiba","PR",110,3.80,4.90,80,120,18.0),
            cidade("Maringá","PR",100,3.50,4.50,60,90,18.0),
            cidade("Cascavel","PR",280,4.20,5.40,100,150,18.0),
            cidade("Foz do Iguaçu","PR",380,4.50,5.80,120,180,18.0),
            cidade("Rio de Janeiro","RJ",900,6.50,8.50,320,480,12.0),
            cidade("Belo Horizonte","MG",700,5.80,7.50,250,380,12.0),
            cidade("Porto Alegre","RS",900,6.20,8.00,300,450,12.0),
            cidade("Florianópolis","SC",580,5.40,7.00,220,330,12.0),
            cidade("Joinville","SC",500,5.20,6.80,200,300,12.0),
            cidade("Campo Grande","MS",600,5.60,7.20,230,350,7.0),
            cidade("Goiânia","GO",900,6.20,8.00,300,450,7.0),
            cidade("Brasília","DF",1000,6.50,8.50,320,480,7.0),
            cidade("Salvador","BA",1800,8.50,11.00,500,750,7.0),
            cidade("Recife","PE",2500,10.00,13.00,650,980,7.0),
            cidade("Fortaleza","CE",2800,10.50,13.50,700,1050,7.0),
            cidade("Manaus","AM",3500,12.00,15.50,900,1350,7.0),
            cidade("Belém","PA",3000,11.00,14.00,800,1200,7.0),
            cidade("Cuiabá","MT",1200,7.00,9.00,380,570,7.0),
            cidade("Uberlândia","MG",520,5.20,6.80,200,300,12.0),
            cidade("Londrina","PR",0,0,0,0,0,18.0),
            cidade("Maringá","PR",100,3.50,4.50,60,90,18.0),
            cidade("Caxias do Sul","RS",1000,6.50,8.50,320,480,12.0)
        );
        cidadeRepo.saveAll(cidades);
        log.info(cidades.size() + " cidades cadastradas");
    }

    private Produto p(String cod,String desc,String ant,TipoProduto tipo,CategoriaLinhaSeca cat,double fator) {
        return Produto.builder().codigo(cod).descricao(desc).nomeAntigo(ant)
                .tipo(tipo).categoria(cat).fatorQtdM3(fator).ativo(true).build();
    }
    private Produto r(String cod,String desc,double comp,double larg,double alt) {
        return Produto.builder().codigo(cod).descricao(desc)
                .tipo(TipoProduto.REFRIGERADO).comprimento(comp).largura(larg).altura(alt).ativo(true).build();
    }
    private Cliente c(String nome,double fator) {
        return Cliente.builder().nome(nome).fatorDescarga(fator).ativo(true).build();
    }
    private Cidade cidade(String nome,String estado,double km,double ftTruck,double ftCarreta,
                           double pedTruck,double pedCarreta,double icms) {
        return Cidade.builder().nome(nome).estado(estado).distanciaKm(km)
                .freteBaseKmTruck(ftTruck).freteBaseKmCarreta(ftCarreta)
                .pedagioTruck(pedTruck).pedagioCarreta(pedCarreta)
                .icmsPercent(icms).viaApi(false).build();
    }
}
