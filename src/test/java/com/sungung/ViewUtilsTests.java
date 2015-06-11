package com.sungung;

import com.sungung.model.Brewer;
import com.sungung.report.view.ViewUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
public class ViewUtilsTests {
    @Test
    public void getFieldsTest() {
        Map<String, Field> fieldMap = ViewUtils.getFields(Brewer.class);
        assertNotNull(fieldMap.get("BREWER"));
        assertThat(fieldMap.get("BREWER").getName(), is("name"));
    }

    @Test
    public void columnHeaderTest() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(ViewUtils.REPORT_DOMAIN_CLASS, Brewer.class);

        Map<String, Field> fields = ViewUtils.getFields(model);
        assertNotNull(fields.get("BREWER"));

        String[] labels = ViewUtils.getColumnHeaders(model, fields);
        assertThat(labels.length, is(fields.size()));
    }

}
