package com.example.cprmtest.demo.model.repositories;

import com.example.cprmtest.demo.model.entities.Stock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface StockRepository extends CrudRepository<Stock, Long> {
    Optional<Stock> findByTicker(String ticker);
}
