package com.example.cprmtest.demo.portfolio.services.notifier;

import com.example.cprmtest.demo.model.dao.CustomerDao;
import com.example.cprmtest.demo.model.dto.CustomerNotification;
import com.example.cprmtest.demo.model.dto.TickerUpdate;
import com.example.cprmtest.demo.model.entities.Customer;
import com.example.cprmtest.demo.model.entities.Stock;
import com.example.cprmtest.demo.portfolio.services.calculator.NavCalculator;
import com.example.cprmtest.demo.portfolio.services.notifier.pushservice.HTTPPushService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

class NavNotifierTest {
    @Mock
    private ThrottlingService throttlingService;

    @Mock
    private PushServiceWrapper<Double> pushServiceWrapper;

    @Mock
    private NavCalculator navCalculator;

    @Mock
    private CustomerDao customerDao;

    @Mock
    HTTPPushService<Double> pushService;

    @InjectMocks
    NavNotifier navNotifier;

    TickerUpdate u1, u2;
    Stock s1, s2;
    Customer c1, c2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        s1 = new Stock("AAPL", 0.5, 0.5);
        s2 = new Stock("GOOG", 0.5, 0.5);
        s1.setId((long)1);
        s2.setId((long)2);
        u1 = new TickerUpdate(s1, 100.0, 50.0, (long)1000);
        u2 = new TickerUpdate(s2, 150.0, 50.0, (long)1010);
        c1 = new Customer("a");
        c2 = new Customer("b");
        c1.setId((long)1);
        c2.setId((long)2);
        Mockito.when(customerDao.genericFindById(c1.getId())).thenReturn(c1);
        Mockito.when(customerDao.genericFindById(c2.getId())).thenReturn(c2);
    }

    //check whether pushservicewrapper was called with correct parameters
    @Test
    void recordPriceChangeSingleUpdate() throws InterruptedException {
        Mockito.when(throttlingService.shouldThrottle()).thenReturn(false);
        Mockito.when(throttlingService.getUpdates()).thenReturn(List.of(u1));
        HashMap<Long, Double> priceChange = new HashMap<>();
        priceChange.put(c1.getId(), 5.0);
        Mockito.when(navCalculator.recalculateRelevantNavsForStocks(List.of(u1))).thenReturn(priceChange);
        ArgumentCaptor<CustomerNotification> notificationArgument = ArgumentCaptor.forClass(CustomerNotification.class);
        ArgumentCaptor<String> urlArgument = ArgumentCaptor.forClass(String.class);
        Long timeBeforeStart = System.currentTimeMillis();
        navNotifier.recordPriceChange(u1);
        Thread.sleep(500);
        Mockito.verify(throttlingService).addUpdate(u1);
        Mockito.verify(pushServiceWrapper, Mockito.times(1)).sendAndCheck(urlArgument.capture(), notificationArgument.capture());
        Assertions.assertEquals(c1.getUrl(), urlArgument.getValue());
        Assertions.assertEquals(priceChange.get(c1.getId()), notificationArgument.getValue().getPayload());
        Assertions.assertEquals(c1.getId(), notificationArgument.getValue().getCustomerId());
        Assertions.assertTrue(notificationArgument.getValue().getTimestamp() >= timeBeforeStart);
    }

    @Test
    void recordPriceChangeMultipleUpdates() throws InterruptedException {
        Mockito.when(throttlingService.shouldThrottle()).thenReturn(false);
        Mockito.when(throttlingService.getUpdates()).thenReturn(List.of(u1, u2));
        HashMap<Long, Double> priceChange = new HashMap<>();
        priceChange.put(c1.getId(), 5.0);
        priceChange.put(c2.getId(), 20.0);
        Mockito.when(navCalculator.recalculateRelevantNavsForStocks(List.of(u1, u2))).thenReturn(priceChange);
        ArgumentCaptor<CustomerNotification> notificationArgument = ArgumentCaptor.forClass(CustomerNotification.class);
        ArgumentCaptor<String> urlArgument = ArgumentCaptor.forClass(String.class);
        Long timeBeforeStart = System.currentTimeMillis();
        navNotifier.recordPriceChange(u2);
        Thread.sleep(500);
        Mockito.verify(throttlingService).addUpdate(u2);
        Mockito.verify(pushServiceWrapper, Mockito.times(2)).sendAndCheck(urlArgument.capture(), notificationArgument.capture());
        HashSet<String> urlsSet = new HashSet<>();
        urlsSet.add(c1.getUrl());
        urlsSet.add(c2.getUrl());
        HashSet<Long> idSet = new HashSet<>();
        idSet.add(c1.getId());
        idSet.add(c2.getId());
        Assertions.assertEquals(urlsSet, new HashSet<>(urlArgument.getAllValues()));
        Assertions.assertEquals(new HashSet<>(priceChange.values()), notificationArgument.getAllValues().stream()
                .map(CustomerNotification::getPayload).collect(Collectors.toSet()));
        Assertions.assertEquals(idSet, notificationArgument.getAllValues().stream()
                .map(CustomerNotification::getCustomerId).collect(Collectors.toSet()));
        notificationArgument.getAllValues().forEach(
                (n) -> {
                    Assertions.assertTrue(n.getTimestamp() > timeBeforeStart);
                }
        );
    }

    //on throttle no notifications should be sent
    @Test
    void recordPriceChangeThrottled() {
        Mockito.when(throttlingService.shouldThrottle()).thenReturn(true);
        navNotifier.recordPriceChange(u1);
        Mockito.verify(navCalculator, Mockito.times(0)).recalculateRelevantNavsForStocks(Mockito.any());
        Mockito.verify(pushService, Mockito.times(0)).sendAndCheck(Mockito.any());
        Mockito.verify(pushService, Mockito.times(0)).initializeConnection(Mockito.any());
    }

    @Test
    void recordPriceChangeNoNavChanged() {
        Mockito.when(throttlingService.shouldThrottle()).thenReturn(false);
        navNotifier.recordPriceChange(u1);
        Mockito.when(throttlingService.getUpdates()).thenReturn(List.of(u1));
        HashMap<Long, Double> priceChange = new HashMap<>();
        Mockito.when(navCalculator.recalculateRelevantNavsForStocks(List.of(u1))).thenReturn(priceChange);
        Mockito.verify(pushService, Mockito.times(0)).sendAndCheck(Mockito.any());
        Mockito.verify(pushService, Mockito.times(0)).initializeConnection(Mockito.any());
    }
}