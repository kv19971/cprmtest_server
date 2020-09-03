package com.example.cprmtest.demo.assetpricer.pricers;

import com.example.cprmtest.demo.assetpricer.validators.AssetValidator;
import com.example.cprmtest.demo.exceptions.user.PricerException;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.data.util.Pair;

import javax.xml.validation.Validator;
import java.lang.Math;

public class CallOptionAssetPricer extends OptionAssetPricer {

    public CallOptionAssetPricer(AssetValidator validator) {
        super(validator);
    }

    @Override
    public Double priceAsset() {
        double timeToMaturity;
        // assumption: if option expired then its worth nothing
        try {
            timeToMaturity = calculateTimeToMaturity();
        } catch (PricerException e) {
            return 0.0;
        }
        Pair<Double, Double> d1d2 = calculateD1D2(timeToMaturity);
        double d1 = d1d2.getFirst();
        double d2 = d1d2.getSecond();
        //assumption: if option's book value is negative its worth nothing
        double optionValue = ((getStockPrice(getAssetToPrice().getStock().getId()) *
                (new NormalDistribution().cumulativeProbability(d1)))
                - (getAssetToPrice().getOptionDetails().getStrikePrice()
                * Math.pow(Math.E, (-1 * INTEREST_RATE * timeToMaturity))
                * new NormalDistribution().cumulativeProbability(d2)));
        if(optionValue < 0.0) {
            optionValue = 0.0;
        }

        return applyShortingAdjustment(optionValue * getAssetToPrice().getQuantity());
    }
}
