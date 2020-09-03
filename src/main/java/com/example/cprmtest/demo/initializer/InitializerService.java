package com.example.cprmtest.demo.initializer;

import com.example.cprmtest.demo.marketdata.sources.MarketDataSource;
import com.example.cprmtest.demo.model.dao.CustomerDao;
import com.example.cprmtest.demo.portfolio.services.calculator.NavCalculator;
import org.springframework.beans.factory.annotation.Autowired;

//startup initialization service - gets records from db, calculates initial portfolio values
public abstract class InitializerService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    NavCalculator navCalculator;

    @Autowired
    MarketDataSource dataSource;

    public void initialize() {
        customerDao.genericFindAll().forEach((customer) -> {
            double r = navCalculator.calculateInitialPortfolioValue(customer.getId());
            System.out.println(r);
        });

    }
    // start listening to stock ticks
    public void listenToMarket() {
        dataSource.startListening();
    }


}
