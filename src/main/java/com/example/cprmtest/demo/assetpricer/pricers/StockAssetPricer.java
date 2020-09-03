package com.example.cprmtest.demo.assetpricer.pricers;

import com.example.cprmtest.demo.assetpricer.validators.AssetValidator;
import com.example.cprmtest.demo.exceptions.user.PricerException;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;

//asset pricer for regular stocks
public class StockAssetPricer extends AssetPricer {

    public StockAssetPricer(AssetValidator validator) {
        super(validator);
    }

    public Double priceAsset() {
        double price = getStockPrice(getAssetToPrice().getStock().getId()) * getAssetToPrice().getQuantity();
        return applyShortingAdjustment(price);
    }
}
