package com.example.cprmtest.demo.assetvalidator.validators;

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

import java.sql.Date;

class StockAssetValidatorTest {
    StockAssetValidator validator;
    Stock s1;
    PortfolioAsset a1;
    Customer c1;
    @BeforeEach
    void setUp() {
        s1 = new Stock("AAPL", 0.2, 0.8);
        c1 = new Customer();
        c1.setId((long)1);
        a1 = new PortfolioAsset(c1, s1, (long)100, TradeType.LONG, null);
        validator = new StockAssetValidator();

    }
    @Test
    void validateAssetForNegativeQty() {
        a1.setQuantity((long)-100);
        Assertions.assertThrows(PricerException.class, () -> {
            validator.validateAsset(a1);
        });
    }

    @Test
    void validateAssetForZeroQty() {
        a1.setQuantity((long)0);
        Assertions.assertThrows(PricerException.class, () -> {
            validator.validateAsset(a1);
        });
    }

    @Test
    void validateAssetForOptionAsset() {
        a1.setOptionDetails(new OptionDetails(a1, OptionType.PUT, 120.0, Date.valueOf("2020-08-19")));
        Assertions.assertThrows(PricerException.class, () -> {
            validator.validateAsset(a1);
        });
    }

    @Test
    void validateAssetForValidAsset() {
        Assertions.assertDoesNotThrow(() -> {
            validator.validateAsset(a1);
        });
    }
}