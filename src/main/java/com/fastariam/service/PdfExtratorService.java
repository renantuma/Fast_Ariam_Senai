package com.fastariam.service;

import com.fastariam.dto.ItemExtraidoDTO;
import com.fastariam.model.Produto;
import com.fastariam.repository.ProdutoRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.*;

@Service
public class PdfExtratorService {

    private static final Logger log = Logger.getLogger(PdfExtratorService.class.getName());
    private final ProdutoRepository produtoRepository;

    public PdfExtratorService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    private static final Pattern PATTERN_LINHA_TABELA = Pattern.compile(
            "^\\s*(\\d+)\\s+([A-Z0-9\\-\\.]+)\\s+(.+?)\\s+(\\d+)\\s*$", Pattern.MULTILINE);
    private static final Pattern PATTERN_ITEM_COD_QTD = Pattern.compile(
            "([A-Z0-9\\-\\.]{3,20})\\s+(.+?)\\s+(\\d+)\\s*(?:UN|PC|CX|und)?", Pattern.CASE_INSENSITIVE);

    public List<ItemExtraidoDTO> extrair(MultipartFile arquivo) throws IOException {
        String texto = extrairTexto(arquivo);
        log.info("Texto extraído do PDF (" + texto.length() + " chars)");
        List<ItemExtraidoDTO> itens = tentarPadrao1(texto);
        if (itens.isEmpty()) itens = tentarPadrao2(texto);
        if (itens.isEmpty()) itens = new ArrayList<>();
        resolverProdutos(itens);
        return itens;
    }

    private List<ItemExtraidoDTO> tentarPadrao1(String texto) {
        List<ItemExtraidoDTO> itens = new ArrayList<>();
        Matcher m = PATTERN_LINHA_TABELA.matcher(texto);
        while (m.find()) {
            String codigo = m.group(2).trim();
            String descricao = m.group(3).trim();
            try {
                int quantidade = Integer.parseInt(m.group(4).trim());
                if (quantidade > 0 && quantidade <= 9999) {
                    itens.add(ItemExtraidoDTO.builder().codigoOriginal(codigo)
                            .descricaoOriginal(descricao).quantidade(quantidade).identificado(false).build());
                }
            } catch (NumberFormatException ignored) {}
        }
        return itens;
    }

    private List<ItemExtraidoDTO> tentarPadrao2(String texto) {
        List<ItemExtraidoDTO> itens = new ArrayList<>();
        for (String linha : texto.split("\\n")) {
            linha = linha.trim();
            if (linha.length() < 10) continue;
            Matcher m = PATTERN_ITEM_COD_QTD.matcher(linha);
            if (m.find()) {
                String codigo = m.group(1).trim();
                if (codigo.equalsIgnoreCase("CODIGO") || codigo.equalsIgnoreCase("COD")) continue;
                try {
                    int quantidade = Integer.parseInt(m.group(3).trim());
                    if (quantidade > 0) {
                        itens.add(ItemExtraidoDTO.builder().codigoOriginal(codigo)
                                .descricaoOriginal(m.group(2).trim()).quantidade(quantidade)
                                .identificado(false).build());
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return itens;
    }

    private void resolverProdutos(List<ItemExtraidoDTO> itens) {
        for (ItemExtraidoDTO item : itens) {
            Optional<Produto> produtoOpt = produtoRepository.findByCodigoOrNomeAntigo(item.getCodigoOriginal());
            if (produtoOpt.isEmpty() && item.getDescricaoOriginal() != null) {
                List<Produto> encontrados = produtoRepository.buscarPorTermo(item.getDescricaoOriginal());
                if (!encontrados.isEmpty()) produtoOpt = Optional.of(encontrados.get(0));
            }
            produtoOpt.ifPresent(p -> {
                item.setProdutoId(p.getId());
                item.setProdutoNome(p.getDescricao());
                item.setIdentificado(true);
            });
        }
    }

    private String extrairTexto(MultipartFile arquivo) throws IOException {
        try (PDDocument doc = Loader.loadPDF(arquivo.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(doc);
        }
    }
}
