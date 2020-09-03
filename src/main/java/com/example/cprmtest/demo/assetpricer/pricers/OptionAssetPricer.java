package com.example.cprmtest.demo.assetpricer.pricers;

import com.example.cprmtest.demo.assetpricer.validators.AssetValidator;
import com.example.cprmtest.demo.exceptions.user.PricerException;
import org.springframework.data.util.Pair;
import java.util.Calendar;
import java.util.Date;
import java.lang.Math;

public abstract class OptionAssetPricer extends AssetPricer {
    protected static final double INTEREST_RATE = 0.02;

    public OptionAssetPricer(AssetValidator validator) {
        super(validator);
    }

    // calculate time to maturity in years
    protected double calculateTimeToMaturity() {
        Date today = Calendar.getInstance().getTime();
        Date maturityDate = assetToPrice.getOptionDetails().getExpiryDate();
        long differenceInTime = maturityDate.getTime() - today.getTime();
        if(differenceInTime <= 0.0) {
            throw new PricerException("Option has expired!");
        }

        return (differenceInTime / (double)(1000l * 60 * 60 * 24 * 365));
    }

    // calculate both d1 d2 with same time to maturity as timetomaturity is calculated using current system time
    protected Pair<Double, Double> calculateD1D2(double timeToMaturity) {
        double d1 = calculateD1(timeToMaturity);
        double d2 = calculateD2(timeToMaturity);
        return Pair.of(d1, d2);
    }

    private double calculateD1(double timeToMaturity) {
        double S = getStockPrice(assetToPrice.getStock().getId());
        double K = assetToPrice.getOptionDetails().getStrikePrice();
        double annualizedSd = assetToPrice.getStock().getAnnualizedSd();
        return (Math.log(S/K) + (timeToMaturity * (INTEREST_RATE + (Math.pow(annualizedSd, 2)/2))) / (annualizedSd * Math.sqrt(timeToMaturity)));
    }

    private double calculateD2(double timeToMaturity) {
        double D1 = calculateD1(timeToMaturity);
        double annualizedSd = assetToPrice.getStock().getAnnualizedSd();
        return D1 - (annualizedSd * Math.sqrt(timeToMaturity));
    }
}
