package com.sungung;

import com.sungung.model.Brewer;
import com.sungung.service.BrewerRepository;
import com.sungung.service.BrewerSearchCriteria;
import com.sungung.service.BrewerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class DataServiceTests {

    @Autowired
    BrewerRepository brewerRepository;

    @Autowired
    BrewerService brewerService;

    @Test
    public void repositoryIsUpAndRunningTest() {
        Brewer brewer = brewerRepository.findById(1L);
        assertNotNull(brewer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void searchCriteriaNullTest() {
        brewerService.findBrewers(null, null);
    }

    @Test
    public void pageRequestTest() {
        List<Brewer> list = brewerRepository.findAll(new PageRequest(0, 1, Sort.Direction.ASC, "name")).getContent();
        assertThat(list, hasSize(1));
        assertThat(list.get(0).getName(), is(notNullValue()));
    }

    @Test
    public void findFistPageTest() {
        int pageSize = 10;
        Page<Brewer> brewers = brewerRepository.findAll(new PageRequest(0, pageSize));
        assertTrue(brewers.getTotalPages() > 0);
        assertThat(brewers.getContent(), hasSize(pageSize));
    }

    @Test
    public void iteratePageTest() {
        int pageSize = 10;
        Page<Brewer> brewers = brewerRepository.findAll(new PageRequest(0, pageSize));
        assertThat(brewerRepository.findAll(new PageRequest(brewers.getTotalPages(), pageSize)).getContent(), hasSize(0));
    }

    @Test
    public void findByNameAndSuburbanTest() {
        Brewer brewer = brewerRepository.findByNameAndSuburb("Coldstream Brewery", "Coldstream");
        assertThat(brewer.getName(), is("Coldstream Brewery"));
    }

    @Test
    public void brewerServiceSearchTest() {
        Brewer brewer = brewerRepository.findAll(new Sort(Sort.Direction.DESC, "name")).iterator().next();
        BrewerSearchCriteria criteria = new BrewerSearchCriteria(brewer.getName());
        Page<Brewer> rs = brewerService.findBrewers(criteria, null);
        assertEquals(brewer.getName(), rs.getContent().get(0).getName());
    }

    @Test
    public void brewerServicePageTest() {
        int page = 0;
        int pageSize = 5;
        BrewerSearchCriteria criteria = new BrewerSearchCriteria();
        Pageable pageRequest = new PageRequest(page, pageSize);
        Page<Brewer> rs = brewerService.findBrewers(criteria, pageRequest);
        assertEquals(rs.getSize(), pageSize);
        assertTrue(rs.getContent().get(0).getName().compareTo(rs.getContent().get(1).getName()) <= 0);
    }

    @Test
    public void brewerServiceSortTest() {
        int page = 0;
        int pageSize = 5;
        Sort sort = new Sort(Sort.Direction.DESC, "name");
        BrewerSearchCriteria criteria = new BrewerSearchCriteria();
        Pageable pageRequest = new PageRequest(page, pageSize, sort);
        Page<Brewer> rs = brewerService.findBrewers(criteria, pageRequest);
        assertEquals(rs.getSize(), pageSize);
        assertTrue(rs.getContent().get(0).getName().compareTo(rs.getContent().get(1).getName()) > 0);
    }


}
