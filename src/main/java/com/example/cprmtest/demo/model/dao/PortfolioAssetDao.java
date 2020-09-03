package com.example.cprmtest.demo.model.dao;

import com.example.cprmtest.demo.model.entities.PortfolioAsset;
import com.example.cprmtest.demo.model.repositories.PortfolioAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PortfolioAssetDao extends BaseDao<PortfolioAsset> {

    @Autowired
    private PortfolioAssetRepository portfolioAssetRepository;

    //repository find by stock method cached as this is called frequently for notifications
    public List<PortfolioAsset> findByStockIds(List<Long> stockIds) {
        List<PortfolioAsset> allAssets = new LinkedList<>();
        if(stockIds == null) {
            throw new IllegalArgumentException("Please supply stock ids");
        }
        for (Long id : stockIds) {
            allAssets.addAll(portfolioAssetRepository.findByStock_Id(id));
        }
        return allAssets;
    }

    //used for initial nav calculation + tests
    public List<PortfolioAsset> findByCustomerId(Long customerId) {
        return portfolioAssetRepository.findByCustomer_Id(customerId);
    }

    @Override
    protected CrudRepository<PortfolioAsset, Long> getRepository() {
        return portfolioAssetRepository;
    }

    @Override
    protected String getEntityName() {
        return PortfolioAsset.class.toString();
    }
}
