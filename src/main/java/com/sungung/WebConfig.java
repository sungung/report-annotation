package com.sungung;

import com.sungung.report.view.CSVView;
import com.sungung.report.view.PDFView;
import com.sungung.report.view.XLSView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@Configuration
public class WebConfig {

    @Bean(name = "csvView")
    public CSVView getCSVView() {
        return new CSVView();
    }

    @Bean(name = "xlsView")
    public XLSView getXLSView() {
        return new XLSView();
    }

    @Bean(name = "pdfView")
    public PDFView getPDFView() {
        return new PDFView();
    }

}
