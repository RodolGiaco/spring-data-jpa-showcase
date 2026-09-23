package com.rodo_pizzeria.service;

import com.rodo_pizzeria.persistence.entity.CustomerEntity;
import com.rodo_pizzeria.persistence.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerEntity> getAll() {
        return this.customerRepository.findAll();
    }

    public CustomerEntity findByPhone(String phone) {
        return this.customerRepository.findByPhone(phone);
    }
}
