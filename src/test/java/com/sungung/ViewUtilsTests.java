package com.sungung;

import com.sungung.model.Brewer;
import com.sungung.report.view.ViewUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
public class ViewUtilsTests {

    @Test
    public void getFieldsTest() {

        Map<String, Field> fieldMap = ViewUtils.getFields(Brewer.class);
        assertNotNull(fieldMap.get("BREWER"));
    }
}
