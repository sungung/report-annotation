package com.sungung;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ContentViewTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testHtml() throws Exception{
        this.mockMvc.perform(get("/brewer/contacts")).andExpect(status().isOk())
                .andExpect(content().string(containsString("<td>Brewer</td>")));
    }

    @Test
    public void testJson() throws Exception {
        this.mockMvc.perform(get("/brewer/contacts.json")).andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/json")))
                .andExpect(content().string(containsString("\"brewers\" : [")));
    }

    @Test
    public void testXml() throws Exception {
        this.mockMvc.perform(get("/brewer/contacts.xml")).andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/xml")))
                .andExpect(content().string(containsString("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")));
    }

    @Test
    public void testCsv() throws Exception {
        this.mockMvc.perform(get("/brewer/contacts.csv")).andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",containsString("Victoria_Brewer_Contacts.csv")))
                .andExpect(header().string("Content-Type", containsString("text/csv")))
                .andExpect(content().string(containsString("Brewer,")));
    }

    @Test
    public void testPdf() throws Exception {
        this.mockMvc.perform(get("/brewer/contacts.pdf")).andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("Victoria_Brewer_Contacts.pdf")))
                .andExpect(header().string("Content-Type", containsString("application/pdf")));
    }

    @Test
    public void testXls() throws Exception {
        this.mockMvc.perform(get("/brewer/contacts.xls")).andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("Victoria_Brewer_Contacts.xls")))
                .andExpect(header().string("Content-Type", containsString("application/vnd.ms-excel")));
    }

}
