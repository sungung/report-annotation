package com.sungung.report.view;

import com.sungung.report.annotation.ReportField;
import com.sungung.report.annotation.ReportFormatStyle;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
public class XLSView extends AbstractExcelView {
    @Override
    protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HSSFSheet sheet;
        HSSFRow sheetRow;
        HSSFCell cell;

        ViewUtils.setFileName(response, model, "xls");

        // Get predefined styles
        Map<ReportFormatStyle, CellStyle> styles = createStyles(workbook);

        // Create sheet
        sheet = workbook.createSheet(
                model.get(ViewUtils.REPORT_SHEET_LABEL) == null ?
                    ViewUtils.REPORT_SHEET_LABEL : (String)model.get(ViewUtils.REPORT_SHEET_LABEL)
        );

        sheet.setDefaultColumnWidth(12);

        // Add company logo
        InputStream is = (new ClassPathResource(ViewUtils.REPORT_LOGO)).getInputStream();
        int logoIndex = workbook.addPicture(IOUtils.toByteArray(is), Workbook.PICTURE_TYPE_PNG);
        is.close();

        Row titleRow = sheet.createRow(1);
        titleRow.setHeightInPoints(28f);
        cell = getCell(sheet, 1, 0);
        cell.setCellStyle(styles.get(ReportFormatStyle.TITLE));
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
        anchor.setCol1(0);
        anchor.setRow1(1);
        Picture logo = drawing.createPicture(anchor, logoIndex);
        logo.resize(0.6);

        // Insert report title cell
        if (model.get(ViewUtils.REPORT_TITLE) != null) {
            cell = getCell(sheet, 1, 3);
            setText(cell, (String)model.get(ViewUtils.REPORT_TITLE));
            cell.setCellStyle(styles.get(ReportFormatStyle.TITLE));
        }

        // Create mapping name value of ReportField annotation with getter method
        // so getting method return value by name annotation which is member of labels local array
        Map<String, Field> fields = ViewUtils.getFields(model);

        // Use view model as column definitions which is REPORT_COLUMN_LABELS but if column definition are not found,
        // display all ReportField annotated fields in view model
        String[] labels = ViewUtils.getColumnHeaders(model, fields);

        cell = getCell(sheet, 3, 0);
        setText(cell, ViewUtils.getHeaderNotes());
        cell.setCellStyle(styles.get(ReportFormatStyle.NOTE));

        // Insert report column cells
        for (int i = 0; i < labels.length; i++) {
            cell = getCell(sheet, 4, i);
            setText(cell, labels[i].toUpperCase());
            cell.setCellStyle(styles.get(ReportFormatStyle.HEADER));
        }

        // Render report data into the sheet
        List list = (List) model.get(ViewUtils.REPORT_DATA_SETS);
        for (int i=0; i < list.size(); i++) {
            for (int j = 0; j < labels.length; j++) {
                cell = getCell(sheet, i+5, j);
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
                        setText(cell, (String)v);
                    } else if (v instanceof Date) {
                        cell.setCellValue((Date)v);
                    } else if (v instanceof BigDecimal) {
                        cell.setCellValue(((BigDecimal)v).doubleValue());
                    } else if (v instanceof Boolean) {
                        switch (annotatedStyle) {
                            case X:
                                cell.setCellValue(((Boolean)v)?"X":null);
                                break;
                            case YNU:
                                cell.setCellValue(((Boolean)v)?"Y":"N");
                                break;
                        }
                    } else {
                        setText(cell, (String)v);
                    }

                    // apply style if annotated style is defined in styles map
                    if (annotatedStyle != ReportFormatStyle.NONE) {
                        CellStyle style = styles.get(annotatedStyle);
                        if (style != null) {
                            cell.setCellStyle(style);
                        }
                    }

                }
            }
        }

        // resize column to fit contents but reserve for first four columns for title & logo
        for (int i = 4; i < labels.length; ++i) {
            sheet.autoSizeColumn(i);
        }

    }

    private Map<ReportFormatStyle, CellStyle> createStyles(Workbook wb) {

        Map<ReportFormatStyle, CellStyle> styles = new HashMap<ReportFormatStyle, CellStyle>();

        CellStyle style;
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_GENERAL);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(titleFont);
        styles.put(ReportFormatStyle.TITLE, style);

        Font noteFont = wb.createFont();
        noteFont.setFontHeightInPoints((short) 8);
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_GENERAL);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(noteFont);
        styles.put(ReportFormatStyle.NOTE, style);

        Font columnFont = wb.createFont();
        columnFont.setFontHeightInPoints((short)10);
        columnFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        columnFont.setColor(IndexedColors.WHITE.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(columnFont);
        style.setWrapText(true);
        styles.put(ReportFormatStyle.HEADER, style);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setWrapText(true);
        styles.put(ReportFormatStyle.CELL, style);

        CellStyle floatNumber = wb.createCellStyle();
        floatNumber.cloneStyleFrom(style);
        floatNumber.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        styles.put(ReportFormatStyle.FLOAT, floatNumber);

        CellStyle intNumber = wb.createCellStyle();
        intNumber.cloneStyleFrom(style);
        intNumber.setDataFormat(wb.createDataFormat().getFormat("0"));
        styles.put(ReportFormatStyle.INTEGER, intNumber);

        CellStyle money = wb.createCellStyle();
        money.cloneStyleFrom(style);
        money.setDataFormat(wb.createDataFormat().getFormat("$#,##0.00"));
        styles.put(ReportFormatStyle.MONEY, money);

        CellStyle dateOnly = wb.createCellStyle();
        dateOnly.cloneStyleFrom(style);
        dateOnly.setDataFormat(wb.createDataFormat().getFormat("d/m/yy"));
        styles.put(ReportFormatStyle.DATE, dateOnly);

        CellStyle dateTime = wb.createCellStyle();
        dateTime.cloneStyleFrom(style);
        dateTime.setDataFormat(wb.createDataFormat().getFormat("d/m/yy h:mm"));
        styles.put(ReportFormatStyle.DATETIME, dateTime);

        return styles;
    }

}
