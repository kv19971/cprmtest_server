package com.example.cprmtest.demo.assetpricer.validators;

import com.example.cprmtest.demo.exceptions.user.PricerException;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;

public class StockAssetValidator extends AssetValidator {
    @Override
    public void validateAsset(PortfolioAsset asset) {
        super.validateAsset(asset);
        if(asset.getOptionDetails() != null) { // opp. to option validator
            throw new PricerException("Can't apply non-option pricing to option asset");
        }
    }
}
