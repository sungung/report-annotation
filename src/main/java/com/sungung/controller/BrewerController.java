package com.sungung.controller;

import com.sungung.model.Brewer;
import com.sungung.report.view.CSVView;
import com.sungung.report.view.PDFView;
import com.sungung.report.view.ViewUtils;
import com.sungung.report.view.XLSView;
import com.sungung.service.BrewerSearchCriteria;
import com.sungung.service.BrewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@Controller
@RequestMapping(value = "/brewer")
public class BrewerController {

    @Autowired
    private CSVView csvView;

    @Autowired
    private XLSView xlsView;

    @Autowired
    private PDFView pdfView;

    @Autowired
    private BrewerService brewerService;

    @RequestMapping
    public ModelAndView brewers() {
        Page<Brewer> page = brewerService.findBrewers(new BrewerSearchCriteria(), null);
        return new ModelAndView("brewer/list", ViewUtils.REPORT_DATA_SETS, page.getContent());
    }

    @RequestMapping(value = "/contacts")
    public ModelAndView address(HttpServletRequest request) {

        ModelAndView mav = new ModelAndView();

        Page<Brewer> page = brewerService.findBrewers(new BrewerSearchCriteria(), null);

        String uri = request.getRequestURI();
        // PDF report will be created if URL pattern is like /brewer/contacts.pdf and so on
        // Without extension in URL, it will render HTML page.
        String reportType = uri.substring((uri.lastIndexOf(".")+1));

        // Report file name of report
        mav.addObject(ViewUtils.REPORT_FILE_NAME, "Victoria_Brewer_Contacts");
        // Report title
        mav.addObject(ViewUtils.REPORT_TITLE, "2015 Victoria Brewer Contacts");
        // Sheet name when file type is XLS
        mav.addObject(ViewUtils.REPORT_SHEET_LABEL, "Contacts");
        mav.addObject(ViewUtils.REPORT_DOMAIN_CLASS, Brewer.class);
        mav.addObject(ViewUtils.REPORT_DATA_SETS, page.getContent());

        if ("xls".equalsIgnoreCase(reportType)) {
            mav.setView(xlsView);
        } else if ("csv".equalsIgnoreCase(reportType)) {
            mav.setView(csvView);
        } else if ("pdf".equalsIgnoreCase(reportType)) {
            mav.setView(pdfView);
        } else {
            mav.setViewName("brewer/list");
        }

        return mav;

    }
}
