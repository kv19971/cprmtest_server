package com.example.cprmtest.demo.assetpricer.pricers;

import com.example.cprmtest.demo.assetpricer.validators.StockAssetValidator;
import com.example.cprmtest.demo.exceptions.user.PricerException;
import com.example.cprmtest.demo.model.dto.enums.OptionType;
import com.example.cprmtest.demo.model.dto.enums.TradeType;
import com.example.cprmtest.demo.model.entities.Customer;
import com.example.cprmtest.demo.model.entities.OptionDetails;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;
import com.example.cprmtest.demo.model.entities.Stock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

//testing base class methods as well
class StockAssetPricerTest {
    Stock s1;
    PortfolioAsset a1;
    Customer c1;
    StockAssetPricer pricer;
    StockAssetValidator validator;

    @BeforeEach
    void setUp() {
        s1 = new Stock("AAPL", 0.2, 0.8);
        c1 = new Customer();
        c1.setId((long)1);
        a1 = new PortfolioAsset(c1, s1, (long)100, TradeType.LONG, null);
        validator = Mockito.mock(StockAssetValidator.class);
        pricer = new StockAssetPricer(validator);
        ReflectionTestUtils.setField(pricer, "assetToPrice", a1);
    }

    @Test
    void getStockPrice() {
        HashMap<Long, Double> frozenStockPrice = new HashMap<>();
        frozenStockPrice.put(s1.getId(), 100.0);
        ReflectionTestUtils.setField(pricer, "frozenPrices", frozenStockPrice);
        Assertions.assertEquals(100.0, pricer.getStockPrice(s1.getId()));
    }

    @Test
    void setAssetToPrice() {
        pricer.setAssetToPrice(a1);
        Mockito.verify(validator, Mockito.times(1)).validateAsset(a1);
    }

    @Test
    void getStockPriceForNonFrozenStock() {
        Assertions.assertThrows(PricerException.class, () -> {
            pricer.getStockPrice(s1.getId());
        });
    }

    @Test
    void freezeStockPrice() {
        pricer.freezeStockPrice(s1.getId(), 200.0);
        HashMap<Long, Double> frozenPrices = (HashMap<Long, Double>) ReflectionTestUtils.getField(pricer, "frozenPrices");
        Assertions.assertEquals(200.0, frozenPrices.get(s1.getId()));
    }

    @Test
    void priceAsset() {
        HashMap<Long, Double> frozenStockPrice = new HashMap<>();
        frozenStockPrice.put(s1.getId(), 2.0);
        ReflectionTestUtils.setField(pricer, "frozenPrices", frozenStockPrice);
        Assertions.assertEquals(200.0, pricer.priceAsset(), 0.0001);
    }

    @Test
    void priceShortAsset() {
        HashMap<Long, Double> frozenStockPrice = new HashMap<>();
        frozenStockPrice.put(s1.getId(), 2.0);
        ReflectionTestUtils.setField(pricer, "frozenPrices", frozenStockPrice);
        a1.setTradeType(TradeType.SHORT);
        Assertions.assertEquals(-200.0, pricer.priceAsset(), 0.0001);
    }
}