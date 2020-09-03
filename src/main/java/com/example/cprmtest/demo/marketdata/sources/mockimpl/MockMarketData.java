package com.example.cprmtest.demo.marketdata.sources.mockimpl;

import com.example.cprmtest.demo.marketdata.sources.MarketDataSource;
import com.example.cprmtest.demo.model.dao.StockDao;
import com.example.cprmtest.demo.model.entities.Stock;
import com.example.cprmtest.demo.portfolio.services.notifier.NavNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class MockMarketData extends MarketDataSource {
    private final List<Stock> stocks = new ArrayList<>();

    @Autowired
    private StockDao stockDao;

    @Autowired
    private RNGWrapper rngWrapper;

    private HashMap<Stock, Double> initialPrices;


    @Override
    public void startListening() {
        List<Stock> stocks = stockDao.genericFindAll();
        for(Stock s : stocks){ //start separate thread to generate gbm prices for each stock
            Thread thread = new Thread(new GBMPriceGenerator(s, getInceptionValue(s), notifier, rngWrapper));
            thread.start();
        }
    }

    //get value of stock when application starts (e.g. last closing price for start of today's business day)
    @Override
    public double getInceptionValue(Stock stock) {
        if(initialPrices == null) {
            setupInitialPrices();
        }
        return initialPrices.get(stock);
    }

    private void setupInitialPrices() {
        List<Stock> stocks = stockDao.genericFindAll();
        initialPrices = new HashMap<>();
        for(Stock s : stocks) {
            initialPrices.put(s, rngWrapper.getBetweenRange(100,300));
        }
    }
}
