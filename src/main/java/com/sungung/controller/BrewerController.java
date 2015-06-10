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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

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

    @RequestMapping("/contacts")
    public String brewers(Model model) {
        Page<Brewer> page = brewerService.findBrewers(new BrewerSearchCriteria(), null);
        model.addAttribute("brewers", page.getContent());
        //not require this attribute except for xml view which having an issue to  marshaling list data
        //so add single object marshaling for making test happy.
        model.addAttribute("brewer", page.getContent().get(0));
        return "brewer/contacts";
    }

    @RequestMapping("/contacts.xls")
    public ModelAndView excelReport() {
        return instanceModelAndView(xlsView);
    }

    @RequestMapping("/contacts.pdf")
    public ModelAndView pdfReport() {
        return instanceModelAndView(pdfView);
    }

    @RequestMapping("/contacts.csv")
    public ModelAndView Report() {
        return instanceModelAndView(csvView);
    }

    private ModelAndView instanceModelAndView(Object view) {
        ModelAndView mav = new ModelAndView();
        Page<Brewer> page = brewerService.findBrewers(new BrewerSearchCriteria(), null);
        // Report file name of report
        mav.addObject(ViewUtils.REPORT_FILE_NAME, "Victoria_Brewer_Contacts");
        // Report title
        mav.addObject(ViewUtils.REPORT_TITLE, "2015 Victoria Brewer Contacts");
        // Sheet name when file type is XLS
        mav.addObject(ViewUtils.REPORT_SHEET_LABEL, "Contacts");
        mav.addObject(ViewUtils.REPORT_DOMAIN_CLASS, Brewer.class);
        mav.addObject(ViewUtils.REPORT_DATA_SETS, page.getContent());
       if (view instanceof String) {
           mav.setViewName((String) view);
       } else {
           mav.setView((View) view);
       }
        return mav;
    }

}
