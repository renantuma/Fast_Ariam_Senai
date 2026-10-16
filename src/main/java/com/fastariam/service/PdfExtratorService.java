package com.fastariam.service;

import com.fastariam.dto.ItemExtraidoDTO;
import com.fastariam.model.Produto;
import com.fastariam.repository.ProdutoRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfExtratorService {

    private static final Logger log = Logger.getLogger(PdfExtratorService.class.getName());
    private final ProdutoRepository produtoRepository;

    public PdfExtratorService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // Ex.: "1  COD-001  Descrição do item aqui  10"
    private static final Pattern PATTERN_LINHA_TABELA = Pattern.compile(
            "^\\s*\\d+\\s+([A-Z0-9][A-Z0-9\\-./_]{1,24})\\s+(.+?)\\s+([\\d.]{1,7})\\s*(?:UN|PC|CX|und)?\\s*$",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    // Ex.: "COD-001  Descrição do item  10 UN" (sem número de sequência na linha)
    private static final Pattern PATTERN_ITEM_COD_QTD = Pattern.compile(
            "^([A-Z0-9][A-Z0-9\\-./_]{2,24})\\s+(.+?)\\s+([\\d.]{1,7})\\s*(?:UN|PC|CX|und)?\\s*$",
            Pattern.CASE_INSENSITIVE);

    // Linhas de cabeçalho/rodapé/ruído que atrapalham o casamento dos padrões acima.
    private static final Pattern PATTERN_RUIDO = Pattern.compile(
            "^(p[áa]gina\\s*\\d+.*|total\\b.*|subtotal\\b.*|cnpj.*|cpf.*|www\\..*|http\\S*|-{3,}|=+|_{3,})",
            Pattern.CASE_INSENSITIVE);

    private static final Set<String> CODIGOS_IGNORADOS = Set.of("CODIGO", "CÓDIGO", "COD", "ITEM", "REF", "SKU");

    public List<ItemExtraidoDTO> extrair(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new PdfExtracaoException("O arquivo enviado está vazio. Selecione um PDF válido.");
        }

        String texto = extrairTexto(arquivo);
        log.info("Texto extraído de '" + arquivo.getOriginalFilename() + "' (" + texto.length() + " caracteres)");

        if (texto.isBlank()) {
            log.warning("Nenhum texto extraível em '" + arquivo.getOriginalFilename()
                    + "' — provavelmente um PDF escaneado (imagem).");
            return new ArrayList<>();
        }

        String textoLimpo = limparRuido(texto);

        List<ItemExtraidoDTO> itens = tentarPadrao1(textoLimpo);
        String padraoUsado = "tabela-numerada";
        if (itens.isEmpty()) {
            itens = tentarPadrao2(textoLimpo);
            padraoUsado = "codigo-descricao-qtd";
        }
        if (itens.isEmpty()) padraoUsado = "nenhum (0 itens)";

        itens = deduplicar(itens);
        log.info(itens.size() + " item(ns) extraído(s) — padrão: " + padraoUsado);

        resolverProdutos(itens);
        return itens;
    }

    /** Remove linhas de cabeçalho/rodapé/ruído e normaliza espaçamento antes do casamento de padrões. */
    private String limparRuido(String texto) {
        StringBuilder sb = new StringBuilder();
        for (String linha : texto.split("\\r?\\n")) {
            String normalizada = linha.trim().replaceAll("[ \\t]{2,}", " ");
            if (normalizada.isEmpty() || PATTERN_RUIDO.matcher(normalizada).find()) continue;
            sb.append(normalizada).append('\n');
        }
        return sb.toString();
    }

    private List<ItemExtraidoDTO> tentarPadrao1(String texto) {
        List<ItemExtraidoDTO> itens = new ArrayList<>();
        Matcher m = PATTERN_LINHA_TABELA.matcher(texto);
        while (m.find()) {
            adicionarSeValido(itens, m.group(1), m.group(2), m.group(3));
        }
        return itens;
    }

    private List<ItemExtraidoDTO> tentarPadrao2(String texto) {
        List<ItemExtraidoDTO> itens = new ArrayList<>();
        for (String linha : texto.split("\\n")) {
            if (linha.length() < 10) continue;
            Matcher m = PATTERN_ITEM_COD_QTD.matcher(linha);
            if (m.find()) {
                adicionarSeValido(itens, m.group(1), m.group(2), m.group(3));
            }
        }
        return itens;
    }

    private void adicionarSeValido(List<ItemExtraidoDTO> itens, String codigoBruto, String descricaoBruta, String qtdBruta) {
        String codigo = codigoBruto.trim();
        if (CODIGOS_IGNORADOS.contains(codigo.toUpperCase(Locale.ROOT))) return;

        String descricao = descricaoBruta.trim().replaceAll("\\s{2,}", " ");
        if (descricao.isEmpty()) return;

        Integer quantidade = parseQuantidade(qtdBruta);
        if (quantidade == null || quantidade <= 0 || quantidade > 99999) {
            if (quantidade != null) {
                log.fine("Quantidade fora do intervalo esperado, item ignorado: código=" + codigo + " qtd=" + qtdBruta);
            }
            return;
        }

        itens.add(ItemExtraidoDTO.builder().codigoOriginal(codigo)
                .descricaoOriginal(descricao).quantidade(quantidade).identificado(false).build());
    }

    /** Converte números no formato brasileiro (ex.: "1.500" = mil e quinhentos) para inteiro. */
    private Integer parseQuantidade(String bruto) {
        try {
            return Integer.parseInt(bruto.trim().replace(".", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Remove itens duplicados (mesmo código + quantidade capturados por sobreposição de linhas). */
    private List<ItemExtraidoDTO> deduplicar(List<ItemExtraidoDTO> itens) {
        Map<String, ItemExtraidoDTO> vistos = new LinkedHashMap<>();
        for (ItemExtraidoDTO item : itens) {
            String chave = item.getCodigoOriginal().toUpperCase(Locale.ROOT) + "|" + item.getQuantidade();
            vistos.putIfAbsent(chave, item);
        }
        return new ArrayList<>(vistos.values());
    }

    private void resolverProdutos(List<ItemExtraidoDTO> itens) {
        for (ItemExtraidoDTO item : itens) {
            Optional<Produto> produtoOpt = produtoRepository.findByCodigoOrNomeAntigo(item.getCodigoOriginal());
            if (produtoOpt.isEmpty() && item.getDescricaoOriginal() != null) {
                produtoOpt = melhorCandidato(item.getDescricaoOriginal());
            }
            produtoOpt.ifPresent(p -> {
                item.setProdutoId(p.getId());
                item.setProdutoNome(p.getDescricao());
                item.setIdentificado(true);
            });
        }
    }

    /** A busca por termo pode retornar vários produtos parecidos; escolhe o que mais compartilha palavras com a descrição extraída. */
    private Optional<Produto> melhorCandidato(String descricaoExtraida) {
        List<Produto> encontrados = produtoRepository.buscarPorTermo(descricaoExtraida);
        if (encontrados.isEmpty()) return Optional.empty();
        if (encontrados.size() == 1) return Optional.of(encontrados.get(0));

        Set<String> termosBusca = tokenizar(descricaoExtraida);
        Produto melhor = null;
        int melhorPontuacao = -1;
        for (Produto p : encontrados) {
            Set<String> termosProduto = tokenizar(p.getDescricao());
            termosProduto.retainAll(termosBusca);
            int pontuacao = termosProduto.size();
            if (pontuacao > melhorPontuacao) {
                melhorPontuacao = pontuacao;
                melhor = p;
            }
        }
        return Optional.ofNullable(melhor);
    }

    private Set<String> tokenizar(String texto) {
        Set<String> tokens = new HashSet<>();
        for (String palavra : texto.toLowerCase(Locale.ROOT).split("[^a-zà-ú0-9]+")) {
            if (palavra.length() >= 3) tokens.add(palavra);
        }
        return tokens;
    }

    private String extrairTexto(MultipartFile arquivo) throws IOException {
        try (PDDocument doc = Loader.loadPDF(arquivo.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(doc);
        } catch (InvalidPasswordException e) {
            throw new PdfExtracaoException("O PDF está protegido por senha. Remova a proteção e tente novamente.");
        } catch (IOException e) {
            log.log(Level.WARNING, "Falha ao ler PDF '" + arquivo.getOriginalFilename() + "'", e);
            throw new PdfExtracaoException("Não foi possível ler o arquivo. Verifique se ele é um PDF válido e não está corrompido.");
        }
    }
}
