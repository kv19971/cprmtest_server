package com.example.cprmtest.demo.portfolio.api;

import com.example.cprmtest.demo.model.dao.CustomerDao;
import com.example.cprmtest.demo.portfolio.services.calculator.NavCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class NavController {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private NavCalculator navCalculator;

    //endpoint for portfolio nav
    @GetMapping(value="/portfolio/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Double getPortfolioValue(@PathVariable Long customerId) {
        customerDao.genericFindById(customerId); //done to ensure that customer exists in db
        return navCalculator.calculatePortfolioValue(customerId); //get from cache
    }
}
