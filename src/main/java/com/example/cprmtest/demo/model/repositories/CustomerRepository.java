package com.example.cprmtest.demo.model.repositories;

import com.example.cprmtest.demo.model.entities.Customer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    @Cacheable
    Optional<Customer> findById(Long id);

    Optional<Customer> findByUrl(String url);
}
