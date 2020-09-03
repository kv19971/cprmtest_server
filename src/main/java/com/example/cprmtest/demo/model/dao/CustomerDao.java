package com.example.cprmtest.demo.model.dao;
import com.example.cprmtest.demo.exceptions.user.EntityNotFoundException;
import com.example.cprmtest.demo.model.entities.Customer;
import com.example.cprmtest.demo.model.entities.Stock;
import com.example.cprmtest.demo.model.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class CustomerDao extends BaseDao<Customer>{

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    protected CrudRepository<Customer, Long> getRepository() {
        return customerRepository;
    }

    @Override
    protected String getEntityName() {
        return Customer.class.toString();
    }

    //used ocassionally on file initialization + tests
    public Customer findByUrl(String url) {
        Optional<Customer> customer = customerRepository.findByUrl(url);
        if(customer.isPresent()) {
            return customer.get();
        } else {
            throw new EntityNotFoundException(url + " customer url doesnt exist");
        }
    }
}
