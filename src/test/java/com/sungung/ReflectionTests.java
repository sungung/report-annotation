package com.sungung;

import com.sungung.model.Brewer;
import com.sungung.report.annotation.ReportField;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
public class ReflectionTests {

    @Test
    public void readClassFieldTest() throws NoSuchFieldException {
        Class clazz = Brewer.class;
        Field field = clazz.getDeclaredField("beerSelection");
        assertEquals("beerSelection", field.getName());
    }

    @Test
    public void readClassFieldAnnotationTest() throws NoSuchFieldException {
        Class clazz = Brewer.class;
        Field field = clazz.getDeclaredField("name");
        ReportField reportField = field.getAnnotation(ReportField.class);
        assertNotNull(reportField);
        assertEquals("Brewer",reportField.name());
    }
}
