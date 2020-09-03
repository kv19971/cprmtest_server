package com.example.cprmtest.demo.portfolio.services.notifier;

import com.example.cprmtest.demo.model.dao.CustomerDao;
import com.example.cprmtest.demo.model.dto.CustomerNotification;
import com.example.cprmtest.demo.model.dto.TickerUpdate;
import com.example.cprmtest.demo.model.entities.BaseDBEntity;
import com.example.cprmtest.demo.model.entities.Customer;
import com.example.cprmtest.demo.portfolio.services.calculator.NavCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

//handles notifying relevant customers of their new NAV values
@Component
public class NavNotifier {

    Logger logger = LoggerFactory.getLogger(NavNotifier.class);

    @Autowired
    private ThrottlingService throttlingService;

    @Autowired
    private NavCalculator navCalculator;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PushServiceWrapper<Double> pushServiceWrapper;

    //used by marketdatasource to tell notifier to record update in price
    public void recordPriceChange(TickerUpdate update) {
        throttlingService.addUpdate(update); //give update to throttle service
        if(!throttlingService.shouldThrottle()) { // if we shouldnt throttle
            List<TickerUpdate> updates = throttlingService.getUpdates(); //get all updates cached by throttler
            getNewCalculationsAndUrl(updates).thenAcceptAsync((calcAndUrls) -> { //trigger calculations and then send to given hooks
                calcAndUrls.forEach((key, value) -> sendNotification(key, value).thenAcceptAsync((res) -> {})
                        .exceptionally((exception) -> { //to log exceptions
                        futureExceptionHandler(exception);
                        return null;
                    }));
                }).exceptionally((exception) -> {
                    futureExceptionHandler(exception);
                    return null;
                });
        }
    }

    //async function to perform calculations of customer navs
    @Async
    private CompletableFuture<Map<String, CustomerNotification<Double>>> getNewCalculationsAndUrl(List<TickerUpdate> updates) {
        Map<Long, Double> newCustomerNavs = navCalculator.recalculateRelevantNavsForStocks(updates);
        Map<Long, String> customerUrls = newCustomerNavs.keySet()
                .parallelStream()
                .map((id) -> customerDao.genericFindById(id))
                .filter((customer) -> customer.getUrl() != null && !customer.getUrl().isEmpty())
                .collect(Collectors.toUnmodifiableMap(BaseDBEntity::getId, Customer::getUrl)); // get customer id -> their hook urls (where they get nav updates)

        long timestampCalculationsDone = System.currentTimeMillis(); //this is sent to allow receiver to order their notifications
        Map<String, CustomerNotification<Double>> mergedOutput = newCustomerNavs.entrySet().stream()
                    .collect(Collectors.toMap(
                                (entry) -> customerUrls.get(entry.getKey()),
                                (entry) -> new CustomerNotification<>(entry.getKey(), timestampCalculationsDone, entry.getValue())
                            ));
        return CompletableFuture.completedFuture(mergedOutput);
    }

    @Async
    private CompletableFuture<Boolean> sendNotification(String url, CustomerNotification<Double> notification) {
        return CompletableFuture.completedFuture(pushServiceWrapper.sendAndCheck(url, notification));
    }

    private void futureExceptionHandler(Throwable exception) { //log exceptions
        logger.error(exception.getMessage());
        logger.error(Arrays.toString(exception.getStackTrace()));
    }
}
