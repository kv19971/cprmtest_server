package com.example.cprmtest.demo.assetpricer;

import com.example.cprmtest.demo.assetpricer.pricers.AssetPricer;
import com.example.cprmtest.demo.assetpricer.pricers.CallOptionAssetPricer;
import com.example.cprmtest.demo.assetpricer.pricers.PutOptionAssetPricer;
import com.example.cprmtest.demo.assetpricer.pricers.StockAssetPricer;
import com.example.cprmtest.demo.assetpricer.validators.OptionAssetValidator;
import com.example.cprmtest.demo.assetpricer.validators.StockAssetValidator;
import com.example.cprmtest.demo.model.dto.enums.OptionType;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;
import org.springframework.stereotype.Component;

@Component
public class AssetPricerFactory {

    //component gives pricer, which has 1-1 mapping with the asset
    public AssetPricer getPricerForAsset(PortfolioAsset asset) {
        AssetPricer assetPricer;
        if(asset.getOptionDetails() == null) {
            assetPricer = new StockAssetPricer(new StockAssetValidator());
        } else if(asset.getOptionDetails().getOptionType() == OptionType.CALL) {
            assetPricer = new CallOptionAssetPricer(new OptionAssetValidator());
        } else {
            assetPricer = new PutOptionAssetPricer(new OptionAssetValidator());
        }
        assetPricer.setAssetToPrice(asset);
        return assetPricer;
    }
}
