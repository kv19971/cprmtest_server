package com.example.cprmtest.demo.portfolio.services.calculator;

import com.example.cprmtest.demo.assetpricer.AssetPricerFactory;
import com.example.cprmtest.demo.assetpricer.pricers.AssetPricer;
import com.example.cprmtest.demo.marketdata.sources.mockimpl.MockMarketData;
import com.example.cprmtest.demo.model.dao.PortfolioAssetDao;
import com.example.cprmtest.demo.model.dto.TickerUpdate;
import com.example.cprmtest.demo.model.dto.enums.TradeType;
import com.example.cprmtest.demo.model.entities.Customer;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;
import com.example.cprmtest.demo.model.entities.Stock;
import com.example.cprmtest.demo.portfolio.services.calculator.NavCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class NavCalculatorTest {
    @Mock
    private AssetPricerFactory pricerFactory;

    @Mock
    private MockMarketData marketData;

    @Mock
    private PortfolioAssetDao portfolioAssetDao;

    @InjectMocks
    private NavCalculator navCalculator;

    PortfolioAsset a1, a2, a3;
    Customer c1, c2, c3;
    Stock s1, s2;
    AssetPricer p1, p2, p3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        c1 = new Customer("a");
        c1.setId((long)1);
        c2 = new Customer("b");
        c2.setId((long)2);
        c3 = new Customer("b");
        c3.setId((long)3);
        s1 = new Stock("AAPL", 0.5, 0.4);
        s2 = new Stock("MSFT", 0.5, 0.4);
        a1 = new PortfolioAsset(c1, s1, (long)100, TradeType.LONG, null);
        a2 = new PortfolioAsset(c1, s2, (long)200, TradeType.LONG, null);
        a3 = new PortfolioAsset(c2, s2, (long)50, TradeType.LONG, null);
        Mockito.when(portfolioAssetDao.findByCustomerId((long)1)).thenReturn(List.of(a1,a2));
        Mockito.when(portfolioAssetDao.findByCustomerId((long)2)).thenReturn(List.of(a3));
        Mockito.when(portfolioAssetDao.findByCustomerId((long)3)).thenReturn(List.of());
        p1 = Mockito.mock(AssetPricer.class);
        Mockito.when(p1.priceAsset()).thenReturn(100.0);
        p2 = Mockito.mock(AssetPricer.class);
        Mockito.when(p2.priceAsset()).thenReturn(200.0);
        p3 = Mockito.mock(AssetPricer.class);
        Mockito.when(p3.priceAsset()).thenReturn(50.0);
        Mockito.when(pricerFactory.getPricerForAsset(a1)).thenReturn(p1);
        Mockito.when(pricerFactory.getPricerForAsset(a2)).thenReturn(p2);
        Mockito.when(pricerFactory.getPricerForAsset(a3)).thenReturn(p3);
        Mockito.when(marketData.getInceptionValue(s1)).thenReturn(1.0);
        Mockito.when(marketData.getInceptionValue(s2)).thenReturn(1.0);
    }

    @Test
    void calculatePortfolioValue() {
        //calculate value at inception price
        Assertions.assertEquals(300, navCalculator.calculateInitialPortfolioValue(c1.getId()));
        //check if cache populated
        Assertions.assertEquals(300, navCalculator.calculatePortfolioValue(c1.getId()));
    }

    @Test
    void calculatePortfolioValueGetThroughCache() {
        Assertions.assertEquals(300, navCalculator.calculateInitialPortfolioValue(c1.getId()));
        Assertions.assertEquals(300, navCalculator.calculatePortfolioValue(c1.getId()));
    }

    @Test
    void calculatePortfolioValueForNoAssets() {
        Assertions.assertEquals(0, navCalculator.calculateInitialPortfolioValue(c3.getId()));
        Assertions.assertEquals(0, navCalculator.calculatePortfolioValue(c3.getId()));
    }

    //check if only relevant assets were recalculated + if customer navs were updated correctly
    @Test
    void recalculateRelevantNavsForStocks() {
        Mockito.when(portfolioAssetDao.findByStockIds(List.of(s1.getId(), s2.getId()))).thenReturn(List.of(a1,a2, a3));
        Mockito.doAnswer(new Answer() {
            private int count = 0;
            private double [] prices = {3.0, 1.0};
            public Object answer(InvocationOnMock invocation) {
                double returnValue = prices[count];
                count += 1;
                return returnValue;
            }
        }).when(p1).priceAsset();

        Mockito.doAnswer(new Answer() {
            private int count = 0;
            private double [] prices = {2.0, 1.0};
            public Object answer(InvocationOnMock invocation) {
                double returnValue = prices[count];
                count += 1;
                return returnValue;
            }
        }).when(p2).priceAsset();

        Mockito.doAnswer(new Answer() {
            private int count = 0;
            private double [] prices = {2.0, 1.0};
            public Object answer(InvocationOnMock invocation) {
                double returnValue = prices[count];
                count += 1;
                return returnValue;
            }
        }).when(p3).priceAsset();
        List<TickerUpdate> updates = List.of(new TickerUpdate(s1, 3, 1, (long)100), new TickerUpdate(s2, 2, 1, (long)100));

        ConcurrentHashMap<Long, Double> portfolioCache = new ConcurrentHashMap<>();
        portfolioCache.put(c1.getId(), 0.0);
        portfolioCache.put(c2.getId(), 0.0);
        ReflectionTestUtils.setField(navCalculator, "customerPortfolioNav", portfolioCache);
        Map<Long, Double> changes = navCalculator.recalculateRelevantNavsForStocks(updates);
        Mockito.verify(p1, Mockito.times(1)).freezeStockPrice(s1.getId(), 3.0);
        Mockito.verify(p1, Mockito.times(1)).freezeStockPrice(s1.getId(), 1.0);
        Mockito.verify(p2, Mockito.times(1)).freezeStockPrice(s2.getId(), 2.0);
        Mockito.verify(p2, Mockito.times(1)).freezeStockPrice(s2.getId(), 1.0);
        Mockito.verify(p3, Mockito.times(1)).freezeStockPrice(s2.getId(), 2.0);
        Mockito.verify(p3, Mockito.times(1)).freezeStockPrice(s2.getId(), 1.0);
        Assertions.assertEquals(3.0, changes.get(c1.getId()));
        Assertions.assertEquals(1.0, changes.get(c2.getId()));
    }

    //mainly to check whether customer nav was being updated properly
    @Test
    void recalculateRelevantNavsForStocksMultipleCalculations() {
        Mockito.when(portfolioAssetDao.findByStockIds(List.of(s1.getId(), s2.getId()))).thenReturn(List.of(a1,a2, a3));
        Mockito.doAnswer(new Answer() {
            private int count = 0;
            private double [] prices = {3.0, 1.0, 3.0, 1.0};
            public Object answer(InvocationOnMock invocation) {
                double returnValue = prices[count];
                count += 1;
                return returnValue;
            }
        }).when(p1).priceAsset();

        Mockito.doAnswer(new Answer() {
            private int count = 0;
            private double [] prices = {2.0, 1.0, 2.0, 1.0};
            public Object answer(InvocationOnMock invocation) {
                double returnValue = prices[count];
                count += 1;
                return returnValue;
            }
        }).when(p2).priceAsset();

        Mockito.doAnswer(new Answer() {
            private int count = 0;
            private double [] prices = {2.0, 1.0, 2.0, 1.0};
            public Object answer(InvocationOnMock invocation) {
                double returnValue = prices[count];
                count += 1;
                return returnValue;
            }
        }).when(p3).priceAsset();
        List<TickerUpdate> updates = List.of(new TickerUpdate(s1, 3, 1, (long)100), new TickerUpdate(s2, 2, 1, (long)100));

        ConcurrentHashMap<Long, Double> portfolioCache = new ConcurrentHashMap<>();
        portfolioCache.put(c1.getId(), 0.0);
        portfolioCache.put(c2.getId(), 0.0);
        ReflectionTestUtils.setField(navCalculator, "customerPortfolioNav", portfolioCache);
        navCalculator.recalculateRelevantNavsForStocks(updates);
        Map<Long, Double> changes = navCalculator.recalculateRelevantNavsForStocks(updates);
        Mockito.verify(p1, Mockito.times(2)).freezeStockPrice(s1.getId(), 3.0);
        Mockito.verify(p1, Mockito.times(2)).freezeStockPrice(s1.getId(), 1.0);
        Mockito.verify(p2, Mockito.times(2)).freezeStockPrice(s2.getId(), 2.0);
        Mockito.verify(p2, Mockito.times(2)).freezeStockPrice(s2.getId(), 1.0);
        Mockito.verify(p3, Mockito.times(2)).freezeStockPrice(s2.getId(), 2.0);
        Mockito.verify(p3, Mockito.times(2)).freezeStockPrice(s2.getId(), 1.0);
        Assertions.assertEquals(6.0, changes.get(c1.getId()));
        Assertions.assertEquals(2.0, changes.get(c2.getId()));
    }

    @Test
    void recalculateRelevantNavsForSingleUpdateSingleCustomer() {
        Mockito.when(portfolioAssetDao.findByStockIds(List.of(s2.getId()))).thenReturn(List.of(a3));
        Mockito.doAnswer(new Answer() {
            private int count = 0;
            private double [] prices = {3.0, 1.0};
            public Object answer(InvocationOnMock invocation) {
                double returnValue = prices[count];
                count += 1;
                return returnValue;
            }
        }).when(p3).priceAsset();

        List<TickerUpdate> updates = List.of(new TickerUpdate(s2, 3, 1, (long)100));

        ConcurrentHashMap<Long, Double> portfolioCache = new ConcurrentHashMap<>();
        portfolioCache.put(c1.getId(), 0.0);
        portfolioCache.put(c2.getId(), 0.0);
        ReflectionTestUtils.setField(navCalculator, "customerPortfolioNav", portfolioCache);
        Map<Long, Double> changes = navCalculator.recalculateRelevantNavsForStocks(updates);
        Mockito.verify(p3, Mockito.times(1)).freezeStockPrice(s2.getId(), 3.0);
        Mockito.verify(p3, Mockito.times(1)).freezeStockPrice(s2.getId(), 1.0);
        Assertions.assertEquals(2.0, changes.get(c2.getId()));
    }

    //check if no updates were triggered on irrelevant stocks
    @Test
    void recalculateRelevantNavsForNotBoughtStocks() {
        Mockito.when(portfolioAssetDao.findByStockIds(List.of(s1.getId()))).thenReturn(List.of());

        List<TickerUpdate> updates = List.of(new TickerUpdate(s1, 100, 50, (long)100));

        ConcurrentHashMap<Long, Double> portfolioCache = new ConcurrentHashMap<>();
        portfolioCache.put(c1.getId(), 0.0);
        portfolioCache.put(c2.getId(), 0.0);
        ReflectionTestUtils.setField(navCalculator, "customerPortfolioNav", portfolioCache);
        Map<Long, Double> changes = navCalculator.recalculateRelevantNavsForStocks(updates);
        Assertions.assertTrue(changes.isEmpty());
    }
}