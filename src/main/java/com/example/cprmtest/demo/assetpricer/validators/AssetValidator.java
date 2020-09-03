package com.example.cprmtest.demo.assetpricer.validators;

import com.example.cprmtest.demo.exceptions.user.PricerException;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;

//validator strategy base class
public abstract class AssetValidator {
    public void validateAsset(PortfolioAsset asset) {
        if(asset.getQuantity() <= 0) { //valid for all assets
            throw new PricerException("Asset qty cannot be less than 0!");
        }
    }
}
