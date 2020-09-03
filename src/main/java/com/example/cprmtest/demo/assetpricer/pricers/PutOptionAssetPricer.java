package com.example.cprmtest.demo.assetpricer.pricers;

import com.example.cprmtest.demo.assetpricer.validators.AssetValidator;
import com.example.cprmtest.demo.exceptions.user.PricerException;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.data.util.Pair;

public class PutOptionAssetPricer extends OptionAssetPricer {

    public PutOptionAssetPricer(AssetValidator validator) {
        super(validator);
    }

    @Override
    public Double priceAsset() {
        double timeToMaturity;
        //assumption: if option expired its worth nothing
        try {
            timeToMaturity = calculateTimeToMaturity();
        } catch (PricerException e) {
            return 0.0;
        }
        Pair<Double, Double> d1d2 = calculateD1D2(timeToMaturity);
        double d1 = d1d2.getFirst();
        double d2 = d1d2.getSecond();
        //assumption: if option's book value is negative its worth nothing
        double optionValue = ((getAssetToPrice().getOptionDetails().getStrikePrice()
                * Math.pow(Math.E, (-1 * INTEREST_RATE * timeToMaturity))
                * new NormalDistribution().cumulativeProbability(-1*d2))
                - (getStockPrice(getAssetToPrice().getStock().getId())
                * (new NormalDistribution().cumulativeProbability(-1*d1))));
        if(optionValue < 0) {
            optionValue = 0;
        }
        return applyShortingAdjustment(optionValue * getAssetToPrice().getQuantity());
    }
}
