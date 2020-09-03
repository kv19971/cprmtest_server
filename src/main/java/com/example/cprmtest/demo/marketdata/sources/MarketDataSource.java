package com.example.cprmtest.demo.marketdata.sources;

import com.example.cprmtest.demo.model.entities.Stock;
import com.example.cprmtest.demo.portfolio.services.notifier.NavNotifier;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class MarketDataSource {

    @Autowired
    protected NavNotifier notifier;

    public abstract void startListening();

    public abstract double getInceptionValue(Stock stock);

}
