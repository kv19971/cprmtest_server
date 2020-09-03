package com.example.cprmtest.demo.marketdata.sources.mockimpl;

import com.example.cprmtest.demo.model.dto.TickerUpdate;
import com.example.cprmtest.demo.model.entities.Stock;
import com.example.cprmtest.demo.portfolio.services.notifier.NavNotifier;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;


//geometric brownian motion price generator
public class GBMPriceGenerator implements Runnable {
    private Stock stock;
    private double previousPrice;
    private double secondsAlive;
    private final long MAX_TIME = 7257600;
    private final double minTimeDelay = 0.5;
    private final double maxTimeDelay = 2.0;

    private NavNotifier notifier;
    private RNGWrapper rng;

    public GBMPriceGenerator(Stock stock, double initialPrice, NavNotifier notifier, RNGWrapper rng) {
        this.stock = stock;
        this.previousPrice = initialPrice;
        this.secondsAlive = 0;
        this.notifier = notifier;
        this.rng = rng;
    }

    @Override
    public void run() {
        double newPrice;
        while(true) {
            double timeDelay = rng.getBetweenRange(minTimeDelay, maxTimeDelay);
            secondsAlive += timeDelay;
            newPrice = getNewPrice();
            waitForTime(timeDelay);
            //here we give previous price of the stock to allow the system to calculate previous value of an asset
            notifier.recordPriceChange(new TickerUpdate(this.stock, newPrice, previousPrice, System.currentTimeMillis()));
        }
    }

    //done so thread queuing doesnt get in the way
    private void waitForTime(double timeDelay) {
        long original = System.currentTimeMillis();
        long timeDelayMillis = (long) timeDelay * 1000;
        while (true) {
            if (System.currentTimeMillis() - original >= timeDelayMillis) {
                break;
            }
        }
    }

    private double getNewPrice() {
        double e = rng.getInverseNormal();
        double diff = stock.getExpectedReturn() * ((double)secondsAlive/MAX_TIME) + stock.getAnnualizedSd() * e * Math.sqrt(((double)secondsAlive/MAX_TIME));
        return previousPrice * diff + previousPrice;

    }
}
