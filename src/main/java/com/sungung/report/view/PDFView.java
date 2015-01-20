package com.sungung.report.view;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sungung.report.annotation.ReportField;
import com.sungung.report.annotation.ReportFormatStyle;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.List;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
public class PDFView extends AbstractPdfView {

    private static final Font FONT_NORMAL = new Font(Font.COURIER, 6);
    private static final Font FONT_TITLE = new Font(Font.HELVETICA, 24, Font.BOLD);

    @Override
    protected Document newDocument() {
        // Use landscape
        return new Document(PageSize.A4.rotate());
    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ViewUtils.setFileName(response, model, "pdf");

        // Construct report title.
        InputStream is = (new ClassPathResource(ViewUtils.REPORT_LOGO)).getInputStream();
        Image logo = Image.getInstance(IOUtils.toByteArray(is));
        logo.scalePercent(50f,50f);

        PdfPTable tTitle = new PdfPTable(2);
        tTitle.setWidthPercentage(100f);
        tTitle.setWidths(new int[]{1,2});
        tTitle.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell cell = new PdfPCell(logo);
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tTitle.addCell(cell);

        if (model.get(ViewUtils.REPORT_TITLE) != null) {
            Phrase title = new Phrase((String)model.get(ViewUtils.REPORT_TITLE), FONT_TITLE);
            cell = new PdfPCell(title);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(0);
            tTitle.addCell(cell);
        } else {
            tTitle.addCell(new PdfPCell(new Phrase()));
        }

        document.add(tTitle);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        document.add(getParagraph(ViewUtils.getHeaderNotes(), FONT_NORMAL));
        document.add(Chunk.NEWLINE);

        Map<String, Field> fields = ViewUtils.getFields(model);
        String[] labels = ViewUtils.getColumnHeaders(model, fields);

        PdfPTable table=new PdfPTable(labels.length);
        table.setWidthPercentage(100f);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);

        // insert header
        for (int i = 0; i < labels.length; i++) {
            cell = new PdfPCell(new Phrase(labels[i], FONT_NORMAL));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(cell);
        }
        table.setHeaderRows(1);

        List list = (List) model.get(ViewUtils.REPORT_DATA_SETS);

        // insert actual data row
        for (int i=0; i < list.size(); i++) {
            for (int j = 0; j < labels.length; j++) {

                Field f = fields.get(labels[j].toUpperCase());
                ReportField annotation = f.getAnnotation(ReportField.class);
                ReportFormatStyle annotatedStyle = ReportFormatStyle.NONE;

                if (annotation != null) {
                    annotatedStyle = annotation.format();
                }

                f.setAccessible(true);
                Object v = f.get(list.get(i));

                if (v != null) {
                    if (v instanceof String) {
                        addPDFTableCell(table, (String)v, FONT_NORMAL);
                    } else if (v instanceof Date) {
                        addPDFTableCell(table, annotatedStyle.toFormatted((Date)v), FONT_NORMAL);
                    } else if (v instanceof BigDecimal) {
                        addPDFTableCell(table, annotatedStyle.toFormatted((BigDecimal) v), FONT_NORMAL);
                    } else if (v instanceof Boolean) {
                        addPDFTableCell(table, annotatedStyle.toFormatted((Boolean)v), FONT_NORMAL);
                    } else {
                        addPDFTableCell(table, (String) v, FONT_NORMAL);
                    }
                } else {
                    addPDFTableCell(table, null, FONT_NORMAL);
                }
            }
        }

        document.add(table);
    }

    private Paragraph getParagraph(String v, Font font) {
        Paragraph paragraph = new Paragraph(new Phrase(v, font));
        return paragraph;
    }

    private void addPDFTableCell(PdfPTable table, String v, Font font) {
        table.addCell(new PdfPCell(new Phrase(v, font)));
    }

}
