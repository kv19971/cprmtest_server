package com.example.cprmtest.demo.model.dao;

import com.example.cprmtest.demo.model.dto.enums.TradeType;
import com.example.cprmtest.demo.model.entities.Customer;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;
import com.example.cprmtest.demo.model.entities.Stock;
import com.example.cprmtest.demo.model.repositories.PortfolioAssetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioAssetDaoTest {

    @Mock
    private PortfolioAssetRepository portfolioAssetRepository;

    @InjectMocks
    private PortfolioAssetDao portfolioAssetDao;

    PortfolioAsset a1, a2, a3;
    Customer c;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        c = new Customer("");
        c.setId((long)100);
        a1 = new PortfolioAsset(c, new Stock((long)1), (long)100, TradeType.LONG, null);
        a2 = new PortfolioAsset(c, new Stock((long)2), (long)100, TradeType.LONG, null);
        a3 = new PortfolioAsset(new Customer("test"), new Stock((long)1), (long)10, TradeType.SHORT, null);
    }

    @Test
    void findByStockIdsNullCollection() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            portfolioAssetDao.findByStockIds(null);
        });
    }

    @Test
    void findByStockIdsSingle() {
        List<PortfolioAsset> assets2 = new ArrayList<>();
        assets2.add(a2);
        Mockito.when(portfolioAssetRepository.findByStock_Id((long)2)).thenReturn(assets2);
        List<Long> stockIds = new ArrayList<>();
        stockIds.add((long)2);
        Assertions.assertEquals(assets2, portfolioAssetDao.findByStockIds(stockIds));
    }

    @Test
    void findByStockIdsMultipleIds() {
        List<PortfolioAsset> assets1 = new ArrayList<>();
        assets1.add(a1);
        assets1.add(a3);
        Mockito.when(portfolioAssetRepository.findByStock_Id((long)1)).thenReturn(List.of(a1));
        Mockito.when(portfolioAssetRepository.findByStock_Id((long)3)).thenReturn(List.of(a3));
        List<Long> stockIds = new ArrayList<>();
        stockIds.add((long)1);
        stockIds.add((long)3);
        Assertions.assertEquals(assets1, portfolioAssetDao.findByStockIds(stockIds));
    }

    @Test
    void findByCustomerId() {
        List<PortfolioAsset> assets = new ArrayList<>();
        assets.add(a1);
        assets.add(a2);
        Mockito.when(portfolioAssetRepository.findByCustomer_Id((long)100)).thenReturn(assets);
        Assertions.assertEquals(assets, portfolioAssetDao.findByCustomerId((long)100));
    }
}