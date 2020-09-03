package com.example.cprmtest.demo.portfolio.services.calculator;

import com.example.cprmtest.demo.assetpricer.AssetPricerFactory;
import com.example.cprmtest.demo.assetpricer.pricers.AssetPricer;
import com.example.cprmtest.demo.marketdata.sources.MarketDataSource;
import com.example.cprmtest.demo.model.dao.PortfolioAssetDao;
import com.example.cprmtest.demo.model.dto.TickerUpdate;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;
import com.example.cprmtest.demo.model.entities.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

//responsible for getting assets from db, pricing them, caching portfolio navs for customers
@Component
public class NavCalculator {
    @Autowired
    private AssetPricerFactory pricerFactory;

    @Autowired
    private MarketDataSource marketData;

    @Autowired
    private PortfolioAssetDao portfolioAssetDao;

    private ConcurrentHashMap<Long, Double> customerPortfolioNav = new ConcurrentHashMap<>();

    public double calculatePortfolioValue(Long customerId) {
        return customerPortfolioNav.get(customerId);
    }

    //calculate value on app startup, then cache
    public double calculateInitialPortfolioValue(Long customerId) {
        double result = 0.0;
        List<PortfolioAsset> portfolioAssetList = portfolioAssetDao.findByCustomerId(customerId);
        AssetPricer pricer;
        for(PortfolioAsset asset : portfolioAssetList) {
            pricer = pricerFactory.getPricerForAsset(asset);
            pricer.freezeStockPrice(asset.getStock().getId(), marketData.getInceptionValue(asset.getStock())); //use inception price
            result += pricer.priceAsset();
        }
        customerPortfolioNav.put(customerId, result); //cache
        return result;
    }

    //when stock price updates come in, only calculate difference in values (wrt new price and old price) for relevant assets (assets with given stock)
    public Map<Long, Double> recalculateRelevantNavsForStocks(List<TickerUpdate> stockUpdates) {
        Map<Stock, TickerUpdate> stockToUpdates = stockUpdates.stream().collect(Collectors.toMap(TickerUpdate::getStock, Function.identity()));

        HashMap<Long, Double> changedCustomerNav = portfolioAssetDao.findByStockIds(stockUpdates.stream().map(
                (TickerUpdate update) -> update.getStock().getId()
        ).collect(Collectors.toList())) //assets that are of one of the stocks in the updates
                .parallelStream()
                .map((asset) -> {
                    AssetPricer tmpPricer;
                    tmpPricer = pricerFactory.getPricerForAsset(asset); //generate pricer
                    //return pair of customer id, difference in asset value
                    return Pair.of(asset.getCustomer().getId(), getAssetPriceWithStockPrice(tmpPricer, asset.getStock().getId(), stockToUpdates.get(asset.getStock()).getNewPrice()) -
                            getAssetPriceWithStockPrice(tmpPricer, asset.getStock().getId(), stockToUpdates.get(asset.getStock()).getOldPrice()));
                })
                //then group by customer id, get map from customer id -> sum of differences
                .collect(Collectors.groupingBy(Pair::getFirst, HashMap::new,
                        Collectors.summingDouble(Pair::getSecond)));

        synchronized (customerPortfolioNav) { //done to ensure all updates are applied to cache of same state
            updateCustomerNavCache(changedCustomerNav);
            return changedCustomerNav.entrySet().stream().collect(Collectors.toMap( //map of customerid -> new portfolio navs
                    (e) -> e.getKey(),
                    (e) -> customerPortfolioNav.get(e.getKey())
            ));
        }
    }

    private void updateCustomerNavCache(Map<Long, Double> differences) {
        differences.entrySet().forEach((entry) -> {
            customerPortfolioNav.put(entry.getKey(), entry.getValue() + customerPortfolioNav.get(entry.getKey()));
        });
    }

    private double getAssetPriceWithStockPrice(AssetPricer pricer, long stockId, double stockPrice) {
        pricer.freezeStockPrice(stockId, stockPrice);
        return pricer.priceAsset();
    }
}
