package com.example.cprmtest.demo.assetpricer.pricers;

import com.example.cprmtest.demo.assetpricer.validators.OptionAssetValidator;
import com.example.cprmtest.demo.model.dto.enums.OptionType;
import com.example.cprmtest.demo.model.dto.enums.TradeType;
import com.example.cprmtest.demo.model.entities.Customer;
import com.example.cprmtest.demo.model.entities.OptionDetails;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;
import com.example.cprmtest.demo.model.entities.Stock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Calendar;
import java.util.Date;

class PutOptionAssetPricerTest {
    Stock s1;
    PortfolioAsset a1;
    Customer c1;
    PutOptionAssetPricer pricer;
    OptionAssetValidator validator;

    final double delta  = 0.00001;

    @BeforeEach
    void setUp() {
        s1 = new Stock("AAPL", 0.3, 0.8);
        s1.setId((long)1);
        c1 = new Customer();
        c1.setId((long)1);
        a1 = new PortfolioAsset(c1, s1, (long)100, TradeType.LONG, null);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 10);
        Date expiryDate = c.getTime();
        a1.setOptionDetails(new OptionDetails(a1, OptionType.PUT, 120.0, expiryDate));
        validator = Mockito.mock(OptionAssetValidator.class);
        pricer = new PutOptionAssetPricer(validator);
        ReflectionTestUtils.setField(pricer, "assetToPrice", a1);
        pricer.freezeStockPrice(s1.getId(), 100.0);
    }

    @Test
    void priceAsset() {
        Assertions.assertEquals(3528.475721552779, pricer.priceAsset(), delta);
    }

    @Test
    void priceAssetExpiredOption() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        Date expiryDate = c.getTime();
        a1.getOptionDetails().setExpiryDate(expiryDate);
        Assertions.assertEquals(0.0, pricer.priceAsset(), delta);
    }

    @Test
    void priceAssetShortSell() {
        a1.setTradeType(TradeType.SHORT);
        Assertions.assertEquals(-3528.475721552779, pricer.priceAsset(), delta);
    }

    @Test
    void priceAssetVeryHighStockPrice() {
        pricer.freezeStockPrice(s1.getId(), 1000.0);
        Assertions.assertEquals(60.10048154358092, pricer.priceAsset(),delta);
    }

    @Test
    void priceAssetVeryHighStockSd() {
        s1.setAnnualizedSd(0.9);
        Assertions.assertEquals(8262.671109581415, pricer.priceAsset(), delta);
    }

    @Test
    void priceAssetLowStockSd() {
        s1.setAnnualizedSd(0.02);
        Assertions.assertEquals(2.6922459695522676, pricer.priceAsset(), delta);
    }

    @Test
    void priceAssetCloseToMaturity() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        Date expiryDate = c.getTime();
        a1.getOptionDetails().setExpiryDate(expiryDate);
        Assertions.assertEquals(1490.3885828606612, pricer.priceAsset(), delta);
    }
}