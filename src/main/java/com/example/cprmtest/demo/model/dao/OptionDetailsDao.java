package com.example.cprmtest.demo.model.dao;

import com.example.cprmtest.demo.model.entities.OptionDetails;
import com.example.cprmtest.demo.model.repositories.OptionDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public class OptionDetailsDao extends BaseDao<OptionDetails>{

    @Autowired
    private OptionDetailsRepository optionDetailsRepository;

    @Override
    protected CrudRepository<OptionDetails, Long> getRepository() {
        return optionDetailsRepository;
    }

    @Override
    protected String getEntityName() {
        return OptionDetails.class.toString();
    }
}
