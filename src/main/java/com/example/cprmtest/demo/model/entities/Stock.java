package com.example.cprmtest.demo.model.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
public class Stock extends BaseDBEntity {
    @Column(unique = true)
    String ticker;

    @Column
    Double annualizedSd;

    @Column
    Double expectedReturn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return getTicker().equals(stock.getTicker());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTicker());
    }

    public Stock(long stockId) {
        setId(stockId);
    }

    public Stock(String ticker, Double annualizedSd, Double expectedReturn) {
        this.ticker = ticker;
        setId((long) ticker.hashCode());
        this.annualizedSd = annualizedSd;
        this.expectedReturn = expectedReturn;
    }

    public Stock() {

    }

    @Override
    public String toString() {
        return "Stock{" +
                "ticker='" + ticker + '\'' +
                ", annualizedSd=" + annualizedSd +
                ", expectedReturn=" + expectedReturn +
                '}';
    }

    public String getTicker() {
        return ticker;
    }

    public Double getAnnualizedSd() {
        return annualizedSd;
    }

    public void setAnnualizedSd(Double annualizedSd) {
        this.annualizedSd = annualizedSd;
    }

    public void setExpectedReturn(Double expectedReturn) {
        this.expectedReturn = expectedReturn;
    }

    public Double getExpectedReturn() {
        return expectedReturn;
    }
}
