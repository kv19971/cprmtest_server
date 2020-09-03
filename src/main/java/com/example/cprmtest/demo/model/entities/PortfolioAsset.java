package com.example.cprmtest.demo.model.entities;

import com.example.cprmtest.demo.model.dto.enums.TradeType;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity
public class PortfolioAsset extends BaseDBEntity {
    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    Stock stock;

    @Column
    Long quantity;

    @Column
    TradeType tradeType;

    @OneToOne
    @Nullable
    @JoinColumn(name = "id")
    OptionDetails optionDetails;

    public PortfolioAsset() {

    }

    public PortfolioAsset(Customer customer, Stock stock, Long quantity, TradeType tradeType, @Nullable OptionDetails optionDetails) {
        this.customer = customer;
        this.stock = stock;
        this.quantity = quantity;
        this.tradeType = tradeType;
        this.optionDetails = optionDetails;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public OptionDetails getOptionDetails() {
        return optionDetails;
    }

    public void setOptionDetails(OptionDetails optionDetails) {
        this.optionDetails = optionDetails;
    }
}
