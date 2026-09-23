package com.rodo_pizzeria.web.controller;

import com.rodo_pizzeria.persistence.entity.CustomerEntity;
import com.rodo_pizzeria.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerEntity>> getAll() {
        return ResponseEntity.ok(this.customerService.getAll());
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<CustomerEntity> getByPhone(@PathVariable String phone) {
        return ResponseEntity.ofNullable(this.customerService.findByPhone(phone));
    }
}
