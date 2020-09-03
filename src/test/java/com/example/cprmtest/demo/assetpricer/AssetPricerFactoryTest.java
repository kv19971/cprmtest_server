package com.example.cprmtest.demo.assetpricer;

import com.example.cprmtest.demo.assetpricer.pricers.AssetPricer;
import com.example.cprmtest.demo.assetpricer.pricers.CallOptionAssetPricer;
import com.example.cprmtest.demo.assetpricer.pricers.PutOptionAssetPricer;
import com.example.cprmtest.demo.assetpricer.pricers.StockAssetPricer;
import com.example.cprmtest.demo.assetpricer.validators.AssetValidator;
import com.example.cprmtest.demo.assetpricer.validators.OptionAssetValidator;
import com.example.cprmtest.demo.assetpricer.validators.StockAssetValidator;
import com.example.cprmtest.demo.model.dto.enums.OptionType;
import com.example.cprmtest.demo.model.dto.enums.TradeType;
import com.example.cprmtest.demo.model.entities.Customer;
import com.example.cprmtest.demo.model.entities.OptionDetails;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;
import com.example.cprmtest.demo.model.entities.Stock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class AssetPricerFactoryTest {
    PortfolioAsset longAsset, shortAsset, callOptionAsset, putOptionAsset;
    Stock s1;
    Customer c1;
    @InjectMocks
    AssetPricerFactory pricerFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        s1 = new Stock("AAPL", 0.3, 0.6);
        c1 = new Customer();
        c1.setId((long)1);
        longAsset = new PortfolioAsset(c1, s1, (long)100, TradeType.LONG, null);
        shortAsset = new PortfolioAsset(c1, s1, (long)100, TradeType.SHORT, null);
        callOptionAsset = new PortfolioAsset(c1, s1, (long)100, TradeType.LONG, null);
        OptionDetails op1 = new OptionDetails(callOptionAsset, OptionType.CALL, 150.0, Date.valueOf("2020-07-28"));
        callOptionAsset.setOptionDetails(op1);
        putOptionAsset = new PortfolioAsset(c1, s1, (long)100, TradeType.LONG, null);
        OptionDetails op2 = new OptionDetails(callOptionAsset, OptionType.PUT, 150.0, Date.valueOf("2020-07-28"));
        putOptionAsset.setOptionDetails(op2);

    }
    @Test
    void getPricerForLongAsset() {
        AssetPricer pricer = pricerFactory.getPricerForAsset(longAsset);
        Assertions.assertDoesNotThrow(() -> {
            StockAssetPricer s = (StockAssetPricer) pricer;
        });
        Assertions.assertDoesNotThrow(() -> {
            StockAssetValidator v = (StockAssetValidator) ReflectionTestUtils.getField(pricer, "validator");
        });

        Assertions.assertEquals(longAsset, pricer.getAssetToPrice());
    }

    @Test
    void getPricerForShortAsset() {
        AssetPricer pricer = pricerFactory.getPricerForAsset(shortAsset);
        Assertions.assertDoesNotThrow(() -> {
            StockAssetPricer s = (StockAssetPricer) pricer;
        });
        Assertions.assertDoesNotThrow(() -> {
            StockAssetValidator v = (StockAssetValidator) ReflectionTestUtils.getField(pricer, "validator");
        });
        Assertions.assertEquals(shortAsset, pricer.getAssetToPrice());
    }

    @Test
    void getPricerForCallOptionAsset() {
        AssetPricer pricer = pricerFactory.getPricerForAsset(callOptionAsset);
        Assertions.assertDoesNotThrow(() -> {
            CallOptionAssetPricer s = (CallOptionAssetPricer) pricer;
        });
        Assertions.assertDoesNotThrow(() -> {
            OptionAssetValidator v = (OptionAssetValidator) ReflectionTestUtils.getField(pricer, "validator");
        });
        Assertions.assertEquals(callOptionAsset, pricer.getAssetToPrice());
    }

    @Test
    void getPricerForPutOptionAsset() {
        AssetPricer pricer = pricerFactory.getPricerForAsset(putOptionAsset);
        Assertions.assertDoesNotThrow(() -> {
            PutOptionAssetPricer s = (PutOptionAssetPricer) pricer;
        });
        Assertions.assertDoesNotThrow(() -> {
            OptionAssetValidator v = (OptionAssetValidator) ReflectionTestUtils.getField(pricer, "validator");
        });
        Assertions.assertEquals(putOptionAsset, pricer.getAssetToPrice());
    }
}