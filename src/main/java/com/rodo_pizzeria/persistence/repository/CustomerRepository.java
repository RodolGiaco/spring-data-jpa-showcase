package com.rodo_pizzeria.persistence.repository;

import com.rodo_pizzeria.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends ListCrudRepository<CustomerEntity, String> {
    /** JPQL query with a named parameter; returns {@code null} when no customer matches. */
    @Query(value = "SELECT c FROM CustomerEntity c WHERE c.phoneNumber = :phone")
    CustomerEntity findByPhone(@Param("phone") String phone);

    /** Overrides the inherited {@code findAll()} with an explicit JPQL query. */
    @Query(value = "SELECT c FROM CustomerEntity c")
    List<CustomerEntity> findAll();

}

