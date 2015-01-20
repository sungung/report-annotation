package com.sungung.report.view;

import com.sungung.report.annotation.ReportField;
import com.sungung.report.annotation.ReportFormatStyle;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
public class CSVView extends AbstractView {
    private static final String CONTENT_TYPE = "text/csv";
    private static final String EXTENSION = ".csv";
    private static final String NEWLINE = "\r\n";

    public CSVView() {
        setContentType(CONTENT_TYPE);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ViewUtils.setFileName(response, model, "csv");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));

        CSVFormat csvFormat = CSVFormat.EXCEL.withRecordSeparator(NEWLINE);
        CSVPrinter printer = new CSVPrinter(writer, csvFormat);

        Map<String, Field> fields = ViewUtils.getFields(model);
        String[] labels = ViewUtils.getColumnHeaders(model, fields);

        // Insert column header
        printer.printRecord(labels);

        List list = (List) model.get(ViewUtils.REPORT_DATA_SETS);

        // insert actual data row
        for (int i=0; i < list.size(); i++) {

            List csvRec = new ArrayList();

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
                        csvRec.add((String) v);
                    } else if (v instanceof Date) {
                        csvRec.add(annotatedStyle.toFormatted((Date) v));
                    } else if (v instanceof BigDecimal) {
                        csvRec.add(annotatedStyle.toFormatted((BigDecimal) v));
                    } else if (v instanceof Boolean) {
                        csvRec.add(annotatedStyle.toFormatted((Boolean) v));
                    } else {
                        csvRec.add((String) v);
                    }
                } else {
                    csvRec.add(null);
                }
            }
            printer.printRecord(csvRec);
        }

        writer.flush();
        writer.close();
        printer.close();

        writeToResponse(response, baos);

    }
}
