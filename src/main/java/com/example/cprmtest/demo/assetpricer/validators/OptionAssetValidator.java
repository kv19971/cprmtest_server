package com.example.cprmtest.demo.assetpricer.validators;

import com.example.cprmtest.demo.exceptions.user.PricerException;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;

public class OptionAssetValidator extends AssetValidator {
    @Override
    public void validateAsset(PortfolioAsset asset) {
        super.validateAsset(asset);
        if(asset.getOptionDetails() == null) { //if no option properties then cant do option pricing
            throw new PricerException("Can't apply option pricing to non-option asset");
        }
    }
}
