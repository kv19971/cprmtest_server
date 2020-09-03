package com.example.cprmtest.demo.marketdata.sources.mockimpl;

import com.example.cprmtest.demo.model.dao.StockDao;
import com.example.cprmtest.demo.model.dto.TickerUpdate;
import com.example.cprmtest.demo.model.entities.Stock;
import com.example.cprmtest.demo.portfolio.services.notifier.NavNotifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MockMarketDataTest {
    @Mock
    private NavNotifier notifier;

    @Mock
    private StockDao stockDao;

    @Mock
    private RNGWrapper rngWrapper;

    @InjectMocks
    private MockMarketData marketData;

    Stock s1, s2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(rngWrapper.getInverseNormal()).thenReturn(0.4);
        Mockito.when(rngWrapper.getBetweenRange(100, 300)).thenReturn(220.0);
        Mockito.when(rngWrapper.getBetweenRange(0.5, 2.0)).thenReturn(1.5);
        s1 = new Stock("AAPL", 0.4, 0.7);
        s1.setId((long)1);
        s2 = new Stock("GOOG", 0.2, 0.8);
        s2.setId((long)2);
    }

    @Test
    void startListeningSingleStock() throws InterruptedException {
        Mockito.when(stockDao.genericFindAll()).thenReturn(List.of(s1));
        double expectedNewPrice = 220.0 * 0.00007288397 + 220.0;

        marketData.startListening();
        Mockito.verify(notifier, Mockito.times(0)).recordPriceChange(Mockito.any());
        Thread.sleep(1500);
        ArgumentCaptor<TickerUpdate> tickerUpdateArgumentCaptor = ArgumentCaptor.forClass(TickerUpdate.class);
        Mockito.verify(notifier, Mockito.times(1)).recordPriceChange(tickerUpdateArgumentCaptor.capture());
        Assertions.assertEquals(s1, tickerUpdateArgumentCaptor.getValue().getStock());
        Assertions.assertEquals(220.0, tickerUpdateArgumentCaptor.getValue().getOldPrice());
        Assertions.assertEquals(expectedNewPrice, tickerUpdateArgumentCaptor.getValue().getNewPrice(), 0.000000001);
    }

    @Test
    void startListeningMultipleStocks() throws InterruptedException {
        Mockito.when(stockDao.genericFindAll()).thenReturn(List.of(s1, s2));
        List<Double> newPrices = List.of(220.0 * 0.00007288397 + 220.0, 220.0 * 0.00003653499  + 220.0);
        marketData.startListening();
        Mockito.verify(notifier, Mockito.times(0)).recordPriceChange(Mockito.any());
        Thread.sleep(1500);
        ArgumentCaptor<TickerUpdate> tickerUpdateArgumentCaptor = ArgumentCaptor.forClass(TickerUpdate.class);
        Mockito.verify(notifier, Mockito.times(2)).recordPriceChange(tickerUpdateArgumentCaptor.capture());
        Assertions.assertEquals(new HashSet<>(List.of(s1,s2)), tickerUpdateArgumentCaptor.getAllValues()
                .stream().map(TickerUpdate::getStock).collect(Collectors.toSet()));
        Assertions.assertEquals(new HashSet<>(List.of(220.0, 220.0)), tickerUpdateArgumentCaptor.getAllValues()
                .stream().map(TickerUpdate::getOldPrice).collect(Collectors.toSet()));
        int found = 0;
        for(Double newPrice : newPrices) {
            for(Double actualPrice : tickerUpdateArgumentCaptor.getAllValues().stream().map(TickerUpdate::getNewPrice).collect(Collectors.toList())) {
                if(Math.abs(newPrice-actualPrice) < 0.000000001) {
                    found += 1;
                }
            }
        }
        Assertions.assertEquals(2, found); // check if both new prices in list
    }

    @Test
    void getInceptionValue() {
        Mockito.when(stockDao.genericFindAll()).thenReturn(List.of(s1, s2));
        Assertions.assertEquals(220.0, marketData.getInceptionValue(s1));
        Assertions.assertEquals(220.0, marketData.getInceptionValue(s2));
    }
}