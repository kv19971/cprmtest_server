package com.example.cprmtest.demo.portfolio.services.notifier;

import com.example.cprmtest.demo.model.dto.TickerUpdate;
import com.example.cprmtest.demo.model.entities.Stock;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//throttles updates that come from pricing source. merges updates if they are coming too fast
@Component
public class ThrottlingService {
    // map of stock id -> update
    private final ConcurrentHashMap<Stock, TickerUpdate> tickerUpdatePerStock = new ConcurrentHashMap<>();

    private final static long THROTTLE_MILLIS = 1000; //gap of at least 1sec between updates

    private long lastFlush = 0; //last time stored cache of updates was flushed
    private long lastUpdate = 0; //last incoming update

    public void addUpdate(TickerUpdate update) {
        if(update.getTimestamp() < lastUpdate) { // ensure no out of order updates come. all older delayed updates are ignored
            return;
        }
        lastUpdate = update.getTimestamp();
        tickerUpdatePerStock.compute(update.getStock(), (k,v) -> {
            TickerUpdate newUpdate;
            if(tickerUpdatePerStock.containsKey(k)) {
                newUpdate = mergeTickerUpdates(update, v); //merge new update into relevant old update
            } else {
                newUpdate = update;
            }
            return newUpdate;
        });
    }

    public boolean shouldThrottle() {
        if(System.currentTimeMillis() - lastFlush < (THROTTLE_MILLIS)) {
            return true;
        }
        //if shouldnt throttle then we are ready to flush
        lastFlush = System.currentTimeMillis();
        return false;
    }

    //flush map of stockid -> updates
    public List<TickerUpdate> getUpdates() {
        List<TickerUpdate> updates = new ArrayList<>(tickerUpdatePerStock.values());
        tickerUpdatePerStock.clear();
        return updates;
    }

    public TickerUpdate seekUpdate(Stock stock) {
        return tickerUpdatePerStock.get(stock);
    }

    //merge new update into old stock update, its as if there was one update
    private TickerUpdate mergeTickerUpdates(TickerUpdate newUpdate, TickerUpdate oldUpdate) {
        return new TickerUpdate(newUpdate.getStock(), newUpdate.getNewPrice(), oldUpdate.getOldPrice(), newUpdate.getTimestamp());
    }

    public List<TickerUpdate> seekAllUpdates() {
        return new ArrayList<>(tickerUpdatePerStock.values());
    }
}
