package com.sungung.service;

import com.sungung.model.Brewer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@Component("brewerService")
public class BrewerServiceImpl implements BrewerService {

    private final BrewerRepository brewerRepository;

    @Autowired
    public BrewerServiceImpl(BrewerRepository brewerRepository) {
        this.brewerRepository = brewerRepository;
    }

    @Override
    public Page<Brewer> findBrewers(BrewerSearchCriteria criteria, Pageable pageable) {

        Assert.notNull(criteria, "Criteria cannot be null");
        String name = criteria.getName();

        if (StringUtils.isEmpty(name)) {
            return brewerRepository.findAll(pageable);
        } else {
            return brewerRepository.findByNameIgnoreCase(name.trim(), pageable);
        }

    }
}
