package com.sungung.report.view;

import com.sungung.report.annotation.ReportField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
public class ViewUtils {

    public final static String REPORT_TITLE = "report_title";
    public final static String REPORT_SHEET_LABEL = "sheet_label";
    public final static String REPORT_DATA_SETS = "report_data";
    public final static String REPORT_DOMAIN_CLASS = "domain_class";
    public final static String REPORT_COLUMN_LABELS = "column_labels";
    public final static String REPORT_FILE_NAME = "file_name";
    public final static String REPORT_LOGO = "com/sungung/report/assets/logo.png";

    /***
     * Create mapping name value of ReportField annotation with class member
     *
     * @param  klass class of report model.
     * @return Map cross map between annotated name with actual field.
     */
    public static Map<String, Field> getFields(Class klass) {

        Map<String, Field> fields = new HashMap<String, Field>();

        for (Field f : klass.getDeclaredFields()) {
            ReportField annotation = f.getAnnotation(ReportField.class);
            if (annotation != null) {
                fields.put(annotation.name().toUpperCase(), f);
            }
        }
        return fields;
    }

    public static Map<String, Field> getFields(Map<String, Object> model) {
        Class klass = (Class)model.get(ViewUtils.REPORT_DOMAIN_CLASS);
        return getFields(klass);
    }

    /***
     * Build array of column headers which is defined in UI model if not found,
     * getting them from default annotation of view object.
     *
     * @param model     View model of report controller
     * @param fields    Mapping model object's member
     * @return array of header
     */
    public static String[] getColumnHeaders(Map<String, Object> model, Map<String, Field> fields) {

        String[] labels = new String[0];

        if (model.get(REPORT_COLUMN_LABELS) != null) {
            labels = (String[]) model.get(REPORT_COLUMN_LABELS);
        } else {

            // Sort fields by column index or column name if index is not defined
            List<Field> fieldList = new ArrayList(fields.values());
            Collections.sort(fieldList, new Comparator<Field>() {

                @Override
                public int compare(Field f0, Field f1) {
                    ReportField a0 = f0.getAnnotation(ReportField.class);
                    ReportField a1 = f1.getAnnotation(ReportField.class);

                    if (a0.columnIndex() == a1.columnIndex()) {
                        return a0.name().compareTo(a1.name());
                    } else {
                        return a0.columnIndex() - a1.columnIndex();
                    }
                }
            });

            labels = new String[fieldList.size()];
            for (int i = 0; i < fieldList.size(); i++) {
                labels[i] = fieldList.get(i).getAnnotation(ReportField.class).name();
            }
        }
        return labels;
    }

    public static String getHeaderNotes() {
        return "Reported on "+ DateFormatUtils.format(new Date(), " dd/MMM/yyyy HH:mm")+" AEST";
    }

    public static void setFileName(HttpServletResponse response, Map<String, Object> model, String extension) {

        if (StringUtils.isNotEmpty((String) model.get(REPORT_FILE_NAME))) {
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"" + (String)model.get(REPORT_FILE_NAME) + "." + extension + "\""
            );
        }

    }

}
