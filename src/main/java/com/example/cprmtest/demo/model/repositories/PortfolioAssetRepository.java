package com.example.cprmtest.demo.model.repositories;

import com.example.cprmtest.demo.model.entities.PortfolioAsset;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Component
public interface PortfolioAssetRepository extends CrudRepository<PortfolioAsset, Long> {
    @Cacheable
    List<PortfolioAsset> findByStock_Id(Long stockId);

    List<PortfolioAsset> findByCustomer_Id(Long customerId);
}
