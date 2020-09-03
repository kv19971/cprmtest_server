package com.example.cprmtest.demo.model.entities;

import com.example.cprmtest.demo.model.dto.enums.OptionType;

import javax.persistence.*;
import javax.sound.sampled.Port;
import java.util.Date;

@Entity
public class OptionDetails extends BaseDBEntity{

    public OptionDetails(PortfolioAsset portfolioAsset, OptionType optionType, Double strikePrice, Date expiryDate) {
        this.portfolioAsset = portfolioAsset;
        this.optionType = optionType;
        this.strikePrice = strikePrice;
        this.expiryDate = expiryDate;
    }

    public PortfolioAsset getPortfolioAsset() {
        return portfolioAsset;
    }

    public void setPortfolioAsset(PortfolioAsset portfolioAsset) {
        this.portfolioAsset = portfolioAsset;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }

    public Double getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(Double strikePrice) {
        this.strikePrice = strikePrice;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @OneToOne(mappedBy = "optionDetails")
    PortfolioAsset portfolioAsset;

    @Column
    OptionType optionType;
    @Column
    Double strikePrice;
    @Column
    Date expiryDate;

}
