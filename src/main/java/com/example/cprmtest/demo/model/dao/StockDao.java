package com.example.cprmtest.demo.model.dao;
import com.example.cprmtest.demo.exceptions.user.EntityNotFoundException;
import com.example.cprmtest.demo.model.entities.Stock;
import com.example.cprmtest.demo.model.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StockDao extends BaseDao<Stock>{
    @Autowired
    private StockRepository stockRepository;

    @Override
    protected CrudRepository<Stock, Long> getRepository() {
        return stockRepository;
    }

    @Override
    protected String getEntityName() {
        return Stock.class.toString();
    }

    //used for file initializer + tests
    public Stock findByTicker(String ticker) {
        Optional<Stock> stock = stockRepository.findByTicker(ticker);
        if(stock.isPresent()) {
            return stock.get();
        } else {
            throw new EntityNotFoundException(ticker + " Stock ticker doesnt exist");
        }
    }


}
