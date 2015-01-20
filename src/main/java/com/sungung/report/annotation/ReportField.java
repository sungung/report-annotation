package com.sungung.report.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author parks
 * @since 19/01/15 11:28 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ReportField {
    /**
     * Use field name as default.
     * It is used signifier whether the field will be included in the report
     * if linked list of field name from controller has.
     */
    String name() default "";

    /**
     * Format string for Date or Number
     * e.g Date format dd/MM/yy HH:mm
     *     Number format ###.##
     */
    ReportFormatStyle format() default ReportFormatStyle.NONE;

    /**
     * Index of column in display order
     * Default is 999 so set it to last in order
     * @return
     */
    int columnIndex() default 999;
}
