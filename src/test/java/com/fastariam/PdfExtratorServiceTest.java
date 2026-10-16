package com.fastariam;

import com.fastariam.dto.ItemExtraidoDTO;
import com.fastariam.model.Produto;
import com.fastariam.repository.ProdutoRepository;
import com.fastariam.service.PdfExtracaoException;
import com.fastariam.service.PdfExtratorService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfExtratorServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;
    private PdfExtratorService service;

    @BeforeEach
    void setup() {
        service = new PdfExtratorService(produtoRepository);
    }

    @Test
    void extraiItensDeTabelaNumerada() throws IOException {
        when(produtoRepository.findByCodigoOrNomeAntigo(any())).thenReturn(Optional.empty());
        when(produtoRepository.buscarPorTermo(any())).thenReturn(List.of());

        MockMultipartFile arquivo = pdf("pedido.pdf",
                "1 COD-001 Caixa de Parafusos 10",
                "2 COD-002 Suporte Metalico 1.250");

        List<ItemExtraidoDTO> itens = service.extrair(arquivo);

        assertThat(itens).hasSize(2);
        assertThat(itens.get(0).getCodigoOriginal()).isEqualTo("COD-001");
        assertThat(itens.get(0).getQuantidade()).isEqualTo(10);
        assertThat(itens.get(1).getCodigoOriginal()).isEqualTo("COD-002");
        assertThat(itens.get(1).getQuantidade()).isEqualTo(1250);
    }

    @Test
    void ignoraLinhaDeTotalAoUsarPadraoDeFallback() throws IOException {
        when(produtoRepository.findByCodigoOrNomeAntigo(any())).thenReturn(Optional.empty());
        when(produtoRepository.buscarPorTermo(any())).thenReturn(List.of());

        MockMultipartFile arquivo = pdf("pedido.pdf",
                "COD-001 Caixa de Parafusos 10",
                "TOTAL GERAL 15",
                "COD-002 Suporte Metalico 5");

        List<ItemExtraidoDTO> itens = service.extrair(arquivo);

        assertThat(itens).extracting(ItemExtraidoDTO::getCodigoOriginal)
                .containsExactly("COD-001", "COD-002");
    }

    @Test
    void retornaListaVaziaParaPdfSemTextoExtraivel() throws IOException {
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new PDPage());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);

            MockMultipartFile arquivo = new MockMultipartFile(
                    "arquivo", "vazio.pdf", "application/pdf", baos.toByteArray());

            List<ItemExtraidoDTO> itens = service.extrair(arquivo);
            assertThat(itens).isEmpty();
        }
    }

    @Test
    void lancaExcecaoAmigavelParaArquivoVazio() {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "vazio.pdf", "application/pdf", new byte[0]);

        assertThatThrownBy(() -> service.extrair(arquivo))
                .isInstanceOf(PdfExtracaoException.class);
    }

    @Test
    void lancaExcecaoAmigavelParaArquivoCorrompido() {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "corrompido.pdf", "application/pdf", "isso nao e um pdf".getBytes());

        assertThatThrownBy(() -> service.extrair(arquivo))
                .isInstanceOf(PdfExtracaoException.class);
    }

    @Test
    void lancaExcecaoAmigavelParaPdfProtegidoPorSenha() throws Exception {
        byte[] bytes;
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new PDPage());
            AccessPermission ap = new AccessPermission();
            StandardProtectionPolicy spp = new StandardProtectionPolicy("dono123", "usuario123", ap);
            spp.setEncryptionKeyLength(128);
            doc.protect(spp);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            bytes = baos.toByteArray();
        }

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "protegido.pdf", "application/pdf", bytes);

        assertThatThrownBy(() -> service.extrair(arquivo))
                .isInstanceOf(PdfExtracaoException.class)
                .hasMessageContaining("senha");
    }

    @Test
    void escolheProdutoComMaisPalavrasEmComumQuandoHaVariosCandidatos() throws IOException {
        when(produtoRepository.findByCodigoOrNomeAntigo(any())).thenReturn(Optional.empty());

        Produto poucoParecido = Produto.builder().codigo("X-1").descricao("Suporte generico").build();
        Produto maisParecido = Produto.builder().codigo("X-2").descricao("Suporte Metalico Reforcado").build();
        when(produtoRepository.buscarPorTermo(any())).thenReturn(List.of(poucoParecido, maisParecido));

        MockMultipartFile arquivo = pdf("pedido.pdf", "COD-009 Suporte Metalico 3");

        List<ItemExtraidoDTO> itens = service.extrair(arquivo);

        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).getProdutoNome()).isEqualTo("Suporte Metalico Reforcado");
    }

    private static MockMultipartFile pdf(String nomeArquivo, String... linhas) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                PDType1Font fonte = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                cs.beginText();
                cs.setFont(fonte, 10);
                cs.newLineAtOffset(50, 700);
                for (String linha : linhas) {
                    cs.showText(linha);
                    cs.newLineAtOffset(0, -14);
                }
                cs.endText();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return new MockMultipartFile("arquivo", nomeArquivo, "application/pdf", baos.toByteArray());
        }
    }
}
