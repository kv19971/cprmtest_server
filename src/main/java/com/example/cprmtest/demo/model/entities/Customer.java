package com.example.cprmtest.demo.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;


@Entity
public class Customer extends BaseDBEntity {
    @Column(unique = true)
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                "url=" + getUrl() +
                '}';
    }

    public Customer(String url) {
        this.url = url;
    }

    public Customer() {

    }

    @OneToMany(mappedBy = "customer")
    private List<PortfolioAsset> portfolioAssetList;

    public List<PortfolioAsset> getPortfolioAssetList() {
        return portfolioAssetList;
    }
}
