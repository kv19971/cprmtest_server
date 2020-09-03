package com.example.cprmtest.demo.model.dao;

import com.example.cprmtest.demo.exceptions.user.EntityNotFoundException;
import com.example.cprmtest.demo.model.entities.Stock;
import com.example.cprmtest.demo.model.repositories.StockRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StockDaoTest {
    @Mock
    StockRepository stockRepository;

    @InjectMocks
    StockDao stockDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findByTicker() {
        String ticker = "t";
        Stock stock = new Stock(ticker, 0.5, 0.8);
        Mockito.when(stockRepository.findByTicker(ticker)).thenReturn(Optional.of(stock));

        Assertions.assertEquals(stock, stockDao.findByTicker(ticker));
    }

    @Test
    void findByTickerDoesNotExist() {
        String ticker = "empty";
        Mockito.when(stockRepository.findByTicker(ticker)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            stockDao.findByTicker(ticker);
        });
    }
}