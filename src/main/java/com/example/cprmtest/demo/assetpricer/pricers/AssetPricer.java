package com.example.cprmtest.demo.assetpricer.pricers;

import com.example.cprmtest.demo.assetpricer.validators.AssetValidator;
import com.example.cprmtest.demo.exceptions.user.PricerException;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;
import com.example.cprmtest.demo.model.dto.enums.TradeType;

import java.util.HashMap;

//asset pricer base class
public abstract class AssetPricer {

    PortfolioAsset assetToPrice;

    //map of stockId -> price. supports multiple stocks per asset for future expansion
    HashMap<Long, Double> frozenPrices = new HashMap<>();

    //Asset validator - injected by assetpricerfactory on construction
    private AssetValidator validator;

    public AssetPricer(AssetValidator validator) {
        this.validator = validator;
    }

    //validate before setting!
    public void setAssetToPrice(PortfolioAsset assetToPrice) {
        validator.validateAsset(assetToPrice);
        this.assetToPrice = assetToPrice;
    }

    public double getStockPrice(long stockId) {
        if(frozenPrices.containsKey(stockId)) {
            return frozenPrices.get(stockId);
        }
        throw new PricerException("Frozen stock price does not exist");
    }

    public void freezeStockPrice(long stockId, double price) {
        frozenPrices.put(stockId, price);
    }

    public PortfolioAsset getAssetToPrice() {
        return assetToPrice;
    }

    protected Double applyShortingAdjustment(Double price) {
        if(assetToPrice.getTradeType() == TradeType.SHORT) {
            return -1 * price;
        }
        return price;
    }

    public abstract Double priceAsset();

}
