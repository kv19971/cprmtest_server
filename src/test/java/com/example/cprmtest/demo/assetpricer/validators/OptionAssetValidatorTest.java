package com.example.cprmtest.demo.assetvalidator.validators;

import com.example.cprmtest.demo.assetpricer.validators.OptionAssetValidator;
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

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OptionAssetValidatorTest {
    Stock s1;
    PortfolioAsset a1;
    Customer c1;
    OptionAssetValidator validator;
    
    @BeforeEach
    void setUp() {
        s1 = new Stock("AAPL", 0.2, 0.8);
        c1 = new Customer();
        c1.setId((long)1);
        a1 = new PortfolioAsset(c1, s1, (long)100, TradeType.LONG, null);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 10);
        Date expiryDate = c.getTime();
        a1.setOptionDetails(new OptionDetails(a1, OptionType.CALL, 120.0, expiryDate));
        validator = new OptionAssetValidator();
    }

    @Test
    void validateAssetNoOptionDetails() {
        a1.setOptionDetails(null);
        Assertions.assertThrows(PricerException.class, () -> {
            validator.validateAsset(a1);
        });
    }

    @Test
    void validateAssetValidAsset() {
        Assertions.assertDoesNotThrow(() -> {
            validator.validateAsset(a1);
        });
    }
}