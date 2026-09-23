package com.rodo_pizzeria.service;

import com.rodo_pizzeria.persistence.entity.PizzaEntity;
import com.rodo_pizzeria.persistence.repository.PizzaPageSortRepository;
import com.rodo_pizzeria.persistence.repository.PizzaRepository;
import com.rodo_pizzeria.service.dto.UpdatePizzaPriceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PizzaService {
    private final PizzaRepository pizzaRepository;
    private final PizzaPageSortRepository pizzaPageSortRepository;

    public PizzaService(PizzaRepository pizzaRepository, PizzaPageSortRepository pizzaPageSortRepository) {
        this.pizzaRepository = pizzaRepository;
        this.pizzaPageSortRepository = pizzaPageSortRepository;
    }

    public Page<PizzaEntity> getAll(int page, int elements) {
        Pageable pageRequest = PageRequest.of(page, elements);
        return this.pizzaPageSortRepository.findAll(pageRequest);
    }

    public PizzaEntity get(int idPizza) {
        return this.pizzaRepository.findById(idPizza).orElse(null);
    }

    public PizzaEntity save(PizzaEntity pizzaEntity) {
        return this.pizzaRepository.save(pizzaEntity);
    }

    public void delete(int idPizza) {
        this.pizzaRepository.deleteById(idPizza);
    }

    /** Runs the {@code @Modifying} query, which needs an active transaction. */
    @Transactional
    public void updatePrice(UpdatePizzaPriceDto dto) {
        this.pizzaRepository.updatePrice(dto);
    }

    public Boolean exist(int idPizza) {
        return this.pizzaRepository.existsById(idPizza);
    }

    /**
     * Returns a page of available pizzas.
     *
     * @param sortBy        entity property to sort by, e.g. {@code price} or {@code name}
     * @param sortDirection {@code ASC} or {@code DESC}
     */
    public Page<PizzaEntity> getAvailable(int page, int elements, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        PageRequest pageRequest = PageRequest.of(page, elements, sort);

        return this.pizzaPageSortRepository.findByAvailableTrue(pageRequest);
    }

    public Optional<PizzaEntity> getByName(String namePizza) {
        return this.pizzaRepository.findFirstByAvailableTrueAndNameIgnoreCase(namePizza);
    }

    public int countVegan() {
        return this.pizzaRepository.countByVeganTrue();
    }

    public List<PizzaEntity> getWith(String ingredient) {
        return this.pizzaRepository.findAllByAvailableTrueAndDescriptionContainingIgnoreCase(ingredient);
    }

    public List<PizzaEntity> getWithout(String ingredient) {
        return this.pizzaRepository.findAllByAvailableTrueAndDescriptionNotContainingIgnoreCase(ingredient);
    }

    public List<PizzaEntity> getCheapest(Double price) {
        return this.pizzaRepository.findTop3ByAvailableTrueAndPriceLessThanEqualOrderByPriceAsc(price);
    }
}
