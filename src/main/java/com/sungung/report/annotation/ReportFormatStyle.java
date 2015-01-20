package com.sungung.report.annotation;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The style directs how to render annotated field data
 *
 * @author parks
 * @since 19/01/15 9:15 AM
 */
public enum ReportFormatStyle {
    TITLE(null),
    NOTE(null),
    HEADER(null),
    CELL(null),
    DATE("dd/MM/yy"),
    DATETIME("dd/MM/yy HH:mm"),
    INTEGER("#"),
    FLOAT("#.##"),
    MONEY("$0,00#.##"),
    X("X::"),               // X if it is true
    YNU("Y:N:U"),           // Y for true, N for false, U for unknown which is null
    NONE(null);             // no formatting required as default

    private final String format;
    private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "AU"));

    ReportFormatStyle(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public String toFormatted(Date val) {
        if (val != null) {
            return DateFormatUtils.format(val, getFormat());
        } else {
            return null;
        }
    }

    public String toFormatted(BigDecimal val) {
        if (val != null) {
            switch (this) {
                case INTEGER:
                    return val.setScale(0).toString();
                case FLOAT:
                    return val.setScale(2).toString();
                case MONEY:
                    return numberFormat.format(val);
                default:
                    return val.toString();
            }
        }
        return null;
    }

    public String toFormatted(Boolean val) {
        if (val != null) {
            switch (this) {
                case X:
                    return (val?"X":"");
                case YNU:
                    return (val?"Y":"N");
                default:
                    return val.toString();
            }
        }
        return null;
    }

    public String toFormatted(String val) {
        return val;
    }
}
