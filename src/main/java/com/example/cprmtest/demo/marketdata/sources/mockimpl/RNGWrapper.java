package com.example.cprmtest.demo.marketdata.sources.mockimpl;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RNGWrapper {
    public double getBetweenRange(double low, double high) {
        Random r = new Random();
        return low + (high - low) * r.nextDouble();
    }

    public double getInverseNormal() {
        return new NormalDistribution().inverseCumulativeProbability(Math.random());
    }
}
