package com.example.cprmtest.demo.model.dto;

import com.example.cprmtest.demo.model.entities.Stock;

//used to model stock price updates
public class TickerUpdate {
    private Stock stock;
    private Double newPrice;
    private Double oldPrice; //prev price - exists to recalculate asset's old value
    private long timestamp;

    public Stock getStock() {
        return stock;
    }

    public Double getNewPrice() {
        return newPrice;
    }

    public Double getOldPrice() {
        return oldPrice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public TickerUpdate(Stock stock, double newPrice, double oldPrice, long timestamp) {
        this.stock = stock;
        this.newPrice = newPrice;
        this.oldPrice = oldPrice;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TickerUpdate{" +
                "stock=" + stock +
                ", newPrice=" + newPrice +
                ", oldPrice=" + oldPrice +
                ", timestamp=" + timestamp +
                '}';
    }
}
