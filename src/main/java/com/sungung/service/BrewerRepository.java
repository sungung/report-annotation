package com.sungung.service;

import com.sungung.model.Brewer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
public interface BrewerRepository extends BaseRepository<Brewer, Long> {
    Page<Brewer> findByNameIgnoreCase(String name, Pageable pageable);
    Brewer findByNameAndSuburb(String name, String suburban);
}
