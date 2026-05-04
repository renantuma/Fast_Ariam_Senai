package com.fastariam.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastariam.model.*;
import com.fastariam.repository.PropostaRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class PropostaGeracaoService {
    private static final Logger log = LoggerFactory.getLogger(PropostaGeracaoService.class);


    private final PropostaRepository propostaRepository;

    public PropostaGeracaoService(PropostaRepository propostaRepository) {
        this.propostaRepository = propostaRepository;
    }

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    private static final DeviceRgb COR_PRIMARIA = new DeviceRgb(0, 71, 141);    // Azul Fast Ariam
    private static final DeviceRgb COR_SECUNDARIA = new DeviceRgb(235, 94, 40);  // Laranja
    private static final DeviceRgb COR_CINZA_CLARO = new DeviceRgb(245, 246, 248);
    private static final DeviceRgb COR_TEXTO = new DeviceRgb(30, 30, 40);
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String gerarPdf(Pedido pedido, Proposta proposta) throws IOException {
        File dir = new File(uploadDir + "/propostas");
        dir.mkdirs();

        String nomeArquivo = String.format("Proposta_%d_%s.pdf",
                pedido.getId(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
        String caminho = dir.getAbsolutePath() + "/" + nomeArquivo;

        try (PdfDocument pdf = new PdfDocument(new PdfWriter(caminho));
             Document doc = new Document(pdf, PageSize.A4)) {

            doc.setMargins(40, 50, 40, 50);

            adicionarCabecalho(doc, pedido);
            adicionarDivisor(doc);
            adicionarDadosGerais(doc, pedido);
            adicionarDivisor(doc);
            adicionarTabelaItens(doc, pedido.getItens());
            adicionarDivisor(doc);
            adicionarVolumetria(doc, pedido);
            adicionarDivisor(doc);
            adicionarDetalheFrete(doc, proposta);
            adicionarDivisor(doc);
            adicionarRodape(doc, pedido);
        }

        proposta.setArquivoPdf(caminho);
        propostaRepository.save(proposta);

        log.info("Proposta PDF gerada: {}", caminho);
        return caminho;
    }

    private void adicionarCabecalho(Document doc, Pedido pedido) {
        // Fundo azul no cabeçalho
        Table header = new Table(UnitValue.createPercentArray(new float[]{60, 40})).useAllAvailableWidth();
        header.setBackgroundColor(COR_PRIMARIA);
        header.setBorder(Border.NO_BORDER);

        // Logo / Nome empresa
        Cell cEmpresa = new Cell().add(
                new Paragraph("FAST ARIAM")
                        .setFontSize(22).setBold().setFontColor(ColorConstants.WHITE)
        ).add(
                new Paragraph("Equipamentos de Exposição")
                        .setFontSize(10).setFontColor(new DeviceRgb(180, 210, 255))
        );
        cEmpresa.setBorder(Border.NO_BORDER).setPadding(18);
        header.addCell(cEmpresa);

        // Número da proposta
        String numProposta = "PROPOSTA #" + String.format("%05d", pedido.getId());
        Cell cNum = new Cell().add(
                new Paragraph(numProposta).setFontSize(13).setBold().setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.RIGHT)
        ).add(
                new Paragraph(LocalDateTime.now().format(FMT_DATA))
                        .setFontSize(9).setFontColor(new DeviceRgb(180, 210, 255))
                        .setTextAlignment(TextAlignment.RIGHT)
        );
        cNum.setBorder(Border.NO_BORDER).setPadding(18).setVerticalAlignment(VerticalAlignment.MIDDLE);
        header.addCell(cNum);

        doc.add(header);
    }

    private void adicionarDadosGerais(Document doc, Pedido pedido) {
        Table t = new Table(UnitValue.createPercentArray(new float[]{25, 40, 20, 15})).useAllAvailableWidth();
        t.setBorder(Border.NO_BORDER);

        adicionarCampo(t, "CLIENTE",
                pedido.getCliente() != null ? pedido.getCliente().getNome() : "—");
        adicionarCampo(t, "DESTINO",
                pedido.getCidadeDestino() != null
                        ? pedido.getCidadeDestino().getNome() + "/" + pedido.getCidadeDestino().getEstado()
                        : "—");
        adicionarCampo(t, "Nº PEDIDO",
                pedido.getNumeroPedido() != null ? pedido.getNumeroPedido() : "—");
        adicionarCampo(t, "VEÍCULO",
                pedido.getVeiculoConfirmado() != null
                        ? pedido.getVeiculoConfirmado().getDescricao()
                        : (pedido.getVeiculoSugerido() != null ? pedido.getVeiculoSugerido().getDescricao() + "*" : "—"));

        doc.add(t);
        doc.add(new Paragraph("* Veículo sugerido pelo sistema").setFontSize(7)
                .setFontColor(new DeviceRgb(130, 130, 130)).setMarginTop(2));
    }

    private void adicionarCampo(Table t, String label, String valor) {
        Cell cLabel = new Cell().add(new Paragraph(label).setFontSize(7).setBold()
                .setFontColor(new DeviceRgb(100, 100, 120)));
        Cell cValor = new Cell().add(new Paragraph(valor).setFontSize(10)
                .setFontColor(COR_TEXTO));
        cLabel.setBorder(Border.NO_BORDER).setPaddingBottom(2).setPaddingTop(8);
        cValor.setBorder(Border.NO_BORDER).setPaddingBottom(6);
        t.addCell(cLabel);
        t.addCell(cValor);
    }

    private void adicionarTabelaItens(Document doc, List<ItemPedido> itens) {
        doc.add(new Paragraph("ITENS DO PEDIDO")
                .setFontSize(11).setBold().setFontColor(COR_PRIMARIA).setMarginBottom(6));

        Table t = new Table(UnitValue.createPercentArray(new float[]{12, 38, 12, 15, 23})).useAllAvailableWidth();
        t.setBorder(Border.NO_BORDER);

        // Cabeçalho
        for (String col : new String[]{"CÓDIGO", "DESCRIÇÃO", "QTD", "TIPO", "VOLUME (m³)"}) {
            t.addHeaderCell(new Cell().add(new Paragraph(col).setFontSize(8).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(COR_PRIMARIA).setBorder(Border.NO_BORDER)
                    .setPadding(6).setTextAlignment(TextAlignment.CENTER));
        }

        boolean zebraToggle = false;
        for (ItemPedido item : itens) {
            DeviceRgb bg = zebraToggle ? COR_CINZA_CLARO : new DeviceRgb(255, 255, 255);
            zebraToggle = !zebraToggle;

            String codigo = item.getProduto() != null ? item.getProduto().getCodigo() : item.getCodigoOriginal();
            String descricao = item.getProduto() != null ? item.getProduto().getDescricao() : item.getDescricaoOriginal();
            String tipo = item.getProduto() != null ? item.getProduto().getTipo().name().replace("_", " ") : "?";

            addCellTabela(t, codigo, bg, TextAlignment.CENTER);
            addCellTabela(t, descricao, bg, TextAlignment.LEFT);
            addCellTabela(t, String.valueOf(item.getQuantidade()), bg, TextAlignment.CENTER);
            addCellTabela(t, tipo, bg, TextAlignment.CENTER);
            addCellTabela(t, formatarNumero(item.getVolumeM3()), bg, TextAlignment.RIGHT);
        }

        doc.add(t);
    }

    private void adicionarVolumetria(Document doc, Pedido pedido) {
        doc.add(new Paragraph("VOLUMETRIA CALCULADA")
                .setFontSize(11).setBold().setFontColor(COR_PRIMARIA).setMarginBottom(6));

        Table t = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        t.setBorder(Border.NO_BORDER);

        adicionarLinhaInfo(t, "Volume Total", formatarNumero(pedido.getVolumeTotalM3()) + " m³");
        adicionarLinhaInfo(t, "Metros de Carroceria", formatarNumero(pedido.getMetrosCarroceria()) + " m");
        adicionarLinhaInfo(t, "Com Margem NVIA (+10%)", formatarNumero(pedido.getMetrosComMargemNvia()) + " m");
        adicionarLinhaInfo(t, "Com Margem Venda (+20%)", formatarNumero(pedido.getMetrosComMargemVenda()) + " m");

        doc.add(t);
    }

    private void adicionarDetalheFrete(Document doc, Proposta proposta) {
        doc.add(new Paragraph("COMPOSIÇÃO DO FRETE")
                .setFontSize(11).setBold().setFontColor(COR_PRIMARIA).setMarginBottom(6));

        Table t = new Table(UnitValue.createPercentArray(new float[]{40, 30, 30})).useAllAvailableWidth();
        t.setBorder(Border.NO_BORDER);

        // Cabeçalho
        for (String col : new String[]{"COMPONENTE", "TRUCK", "CARRETA"}) {
            t.addHeaderCell(new Cell().add(new Paragraph(col).setFontSize(8).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(COR_PRIMARIA).setBorder(Border.NO_BORDER)
                    .setPadding(6).setTextAlignment(TextAlignment.CENTER));
        }

        addLinhaFrete(t, "Frete Base", proposta.getFreteBaseTruck(), proposta.getFreteBaseCarreta(), false);
        addLinhaFrete(t, "Pedágio", proposta.getPedagioTruck(), proposta.getPedagioCarreta(), false);
        addLinhaFrete(t, "Descarga", proposta.getDescargarManualTruck(), proposta.getDescargarManualCarreta(), false);
        addLinhaFrete(t, "Ad Valorem", proposta.getAdValorem(), proposta.getAdValorem(), false);
        addLinhaFrete(t, String.format("Impostos (ICMS %.0f%% + PIS/COFINS %.2f%%)",
                        proposta.getIcmsPercent(), proposta.getPisCofinsPercent()),
                0, 0, false); // simplificado
        addLinhaFrete(t, String.format("Margem Comercial (%.0f%%)", proposta.getMargemComercial()),
                0, 0, false); // simplificado

        // Linha de total destacada
        Cell cTLabel = new Cell().add(new Paragraph("TOTAL ESTIMADO").setFontSize(10).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(COR_SECUNDARIA).setBorder(Border.NO_BORDER).setPadding(8);
        Cell cTTruck = new Cell().add(new Paragraph(formatarMoeda(proposta.getTotalFreteTruck())).setFontSize(10).setBold().setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.RIGHT))
                .setBackgroundColor(COR_SECUNDARIA).setBorder(Border.NO_BORDER).setPadding(8);
        Cell cTCarreta = new Cell().add(new Paragraph(formatarMoeda(proposta.getTotalFreteCarreta())).setFontSize(10).setBold().setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.RIGHT))
                .setBackgroundColor(COR_SECUNDARIA).setBorder(Border.NO_BORDER).setPadding(8);
        t.addCell(cTLabel);
        t.addCell(cTTruck);
        t.addCell(cTCarreta);

        doc.add(t);

        if (proposta.getAjusteManual() != 0) {
            doc.add(new Paragraph(String.format("* Ajuste manual aplicado: %s — %s",
                    formatarMoeda(proposta.getAjusteManual()), proposta.getJustificativaAjuste()))
                    .setFontSize(8).setFontColor(new DeviceRgb(130, 130, 130)).setMarginTop(4));
        }
    }

    private void addLinhaFrete(Table t, String label, double vTruck, double vCarreta, boolean destaque) {
        DeviceRgb bg = destaque ? COR_CINZA_CLARO : new DeviceRgb(255, 255, 255);
        Cell c1 = new Cell().add(new Paragraph(label).setFontSize(9).setFontColor(COR_TEXTO))
                .setBackgroundColor(bg).setBorder(Border.NO_BORDER).setPadding(5);
        Cell c2 = new Cell().add(new Paragraph(vTruck > 0 ? formatarMoeda(vTruck) : "—").setFontSize(9)
                        .setTextAlignment(TextAlignment.RIGHT).setFontColor(COR_TEXTO))
                .setBackgroundColor(bg).setBorder(Border.NO_BORDER).setPadding(5);
        Cell c3 = new Cell().add(new Paragraph(vCarreta > 0 ? formatarMoeda(vCarreta) : "—").setFontSize(9)
                        .setTextAlignment(TextAlignment.RIGHT).setFontColor(COR_TEXTO))
                .setBackgroundColor(bg).setBorder(Border.NO_BORDER).setPadding(5);
        t.addCell(c1);
        t.addCell(c2);
        t.addCell(c3);
    }

    private void adicionarLinhaInfo(Table t, String label, String valor) {
        t.addCell(new Cell().add(new Paragraph(label).setFontSize(9).setFontColor(new DeviceRgb(90, 90, 100)))
                .setBorder(Border.NO_BORDER).setPadding(4));
        t.addCell(new Cell().add(new Paragraph(valor).setFontSize(9).setBold().setFontColor(COR_TEXTO))
                .setBorder(Border.NO_BORDER).setPadding(4));
    }

    private void addCellTabela(Table t, String texto, DeviceRgb bg, TextAlignment align) {
        t.addCell(new Cell().add(new Paragraph(texto != null ? texto : "").setFontSize(8)
                        .setFontColor(COR_TEXTO).setTextAlignment(align))
                .setBackgroundColor(bg).setBorder(Border.NO_BORDER).setPadding(5));
    }

    private void adicionarDivisor(Document doc) {
        doc.add(new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(0.5f))
                .setMarginTop(8).setMarginBottom(8).setStrokeColor(new DeviceRgb(220, 220, 230)));
    }

    private void adicionarRodape(Document doc, Pedido pedido) {
        String obs = pedido.getObservacoes() != null && !pedido.getObservacoes().isBlank()
                ? "Observações: " + pedido.getObservacoes() + "\n\n" : "";
        doc.add(new Paragraph(obs +
                "Esta proposta é válida por 7 dias corridos a partir da data de emissão. " +
                "Os valores de frete são estimativas e podem sofrer variações conforme condições de mercado. " +
                "Informações sujeitas a alteração sem aviso prévio.\n\n" +
                "Fast Ariam Equipamentos — Londrina/PR — www.fastariam.com.br")
                .setFontSize(7).setFontColor(new DeviceRgb(150, 150, 160))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderTop(new SolidBorder(new DeviceRgb(220, 220, 230), 0.5f))
                .setPaddingTop(10).setMarginTop(15));
    }

    private String formatarNumero(double v) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.3f", v);
    }

    private String formatarMoeda(double v) {
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %,.2f", v);
    }
}
