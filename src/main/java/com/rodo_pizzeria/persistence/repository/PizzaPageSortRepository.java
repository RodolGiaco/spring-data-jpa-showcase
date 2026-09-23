package com.rodo_pizzeria.persistence.repository;

import com.rodo_pizzeria.persistence.entity.PizzaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * Paging and sorting repository for pizzas: inherits {@code findAll(Pageable)} and
 * {@code findAll(Sort)}, and adds a paged derived query.
 */
public interface PizzaPageSortRepository extends ListPagingAndSortingRepository<PizzaEntity, Integer> {
    Page<PizzaEntity> findByAvailableTrue(Pageable pageable);
}
