package com.example.cprmtest.demo.portfolio.services.notifier;

import com.example.cprmtest.demo.model.dto.TickerUpdate;
import com.example.cprmtest.demo.model.entities.Stock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThrottlingServiceTest {

    @InjectMocks
    ThrottlingService throttlingService;

    Stock s1, s2;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        s1 = new Stock("AAPL", 0.3, 0.8);
        s1.setId((long)1);
        s2 = new Stock("GOOG", 0.3, 0.8);
        s2.setId((long)2);
    }

    //older updates coming late should be discarded
    @Test
    void addUpdateTimestampBeforeLastAddedUpdate() {
        TickerUpdate update = new TickerUpdate(s1, 100, 50, (long)-100);
        throttlingService.addUpdate(update);
        Assertions.assertEquals(0, throttlingService.seekAllUpdates().size());
    }

    //fresh update for stock
    @Test
    void addUpdateFirstUpdate() {
        TickerUpdate update = new TickerUpdate(s1, 100, 50, (long)100);
        throttlingService.addUpdate(update);
        Assertions.assertEquals(update.getNewPrice(), throttlingService.seekUpdate(s1).getNewPrice(), 0.0001);
        Assertions.assertEquals(update.getOldPrice(), throttlingService.seekUpdate(s1).getOldPrice(), 0.0001);
        Assertions.assertEquals(update.getTimestamp(), throttlingService.seekUpdate(s1).getTimestamp());
        Assertions.assertEquals(1, throttlingService.seekAllUpdates().size());
    }

    //handle multiple stocks
    @Test
    void addUpdateTwoUpdatesDifferentStocks() {
        TickerUpdate update = new TickerUpdate(s1, 100, 50, (long)100);
        TickerUpdate update2 = new TickerUpdate(s2, 70, 30, (long)100);
        throttlingService.addUpdate(update);
        throttlingService.addUpdate(update2);
        Assertions.assertEquals(update.getNewPrice(), throttlingService.seekUpdate(s1).getNewPrice(), 0.0001);
        Assertions.assertEquals(update.getOldPrice(), throttlingService.seekUpdate(s1).getOldPrice(), 0.0001);
        Assertions.assertEquals(update.getTimestamp(), throttlingService.seekUpdate(s1).getTimestamp());
        Assertions.assertEquals(update2.getNewPrice(), throttlingService.seekUpdate(s2).getNewPrice(), 0.0001);
        Assertions.assertEquals(update2.getOldPrice(), throttlingService.seekUpdate(s2).getOldPrice(), 0.0001);
        Assertions.assertEquals(update2.getTimestamp(), throttlingService.seekUpdate(s2).getTimestamp());
        Assertions.assertEquals(2, throttlingService.seekAllUpdates().size());
    }

    //handle updates for 1 stock that come in too soon
    @Test
    void addUpdateMultipleMergingUpdates() {
        TickerUpdate update = new TickerUpdate(s1, 100, 50, (long)100);
        TickerUpdate update2 = new TickerUpdate(s2, 70, 30, (long)100);
        throttlingService.addUpdate(update);
        throttlingService.addUpdate(update2);
        TickerUpdate update3 = new TickerUpdate(s1, 130, 100, (long)150);
        throttlingService.addUpdate(update3);
        Assertions.assertEquals(update3.getNewPrice(), throttlingService.seekUpdate(s1).getNewPrice(), 0.0001);
        Assertions.assertEquals(update.getOldPrice(), throttlingService.seekUpdate(s1).getOldPrice(), 0.0001);
        Assertions.assertEquals(update3.getTimestamp(), throttlingService.seekUpdate(s1).getTimestamp());
        Assertions.assertEquals(update2.getNewPrice(), throttlingService.seekUpdate(s2).getNewPrice(), 0.0001);
        Assertions.assertEquals(update2.getOldPrice(), throttlingService.seekUpdate(s2).getOldPrice(), 0.0001);
        Assertions.assertEquals(update2.getTimestamp(), throttlingService.seekUpdate(s2).getTimestamp());
        Assertions.assertEquals(2, throttlingService.seekAllUpdates().size());
    }

    @Test
    void shouldThrottle() {
        Assertions.assertFalse(throttlingService.shouldThrottle());
        Assertions.assertTrue(throttlingService.shouldThrottle());
    }

    @Test
    void shouldThrottleWithDelay() throws InterruptedException {
        Assertions.assertFalse(throttlingService.shouldThrottle());
        Assertions.assertTrue(throttlingService.shouldThrottle());
        Thread.sleep(1000);
        Assertions.assertFalse(throttlingService.shouldThrottle());
    }

    @Test
    void shouldThrottleWithLongDelay() throws InterruptedException {
        Assertions.assertFalse(throttlingService.shouldThrottle());
        Assertions.assertTrue(throttlingService.shouldThrottle());
        Thread.sleep(5000);
        Assertions.assertFalse(throttlingService.shouldThrottle());
    }

    //check if flush updates working
    @Test
    void getUpdates() {
        TickerUpdate update = new TickerUpdate(s1, 100, 50, (long)100);
        TickerUpdate update2 = new TickerUpdate(s2, 70, 30, (long)100);
        throttlingService.addUpdate(update);
        throttlingService.addUpdate(update2);
        List<TickerUpdate> updates = throttlingService.getUpdates();
        List<TickerUpdate> expectedUpdates = List.of(update, update2);
        Assertions.assertTrue(throttlingService.seekAllUpdates().isEmpty());
        Assertions.assertTrue(expectedUpdates.size() == updates.size()
                && expectedUpdates.containsAll(updates)
                && updates.containsAll(expectedUpdates));
    }
}