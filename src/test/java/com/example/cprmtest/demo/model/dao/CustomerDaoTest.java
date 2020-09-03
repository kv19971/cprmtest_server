package com.example.cprmtest.demo.model.dao;

import com.example.cprmtest.demo.exceptions.user.EntityNotFoundException;
import com.example.cprmtest.demo.model.entities.Customer;
import com.example.cprmtest.demo.model.repositories.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

//testing base class methods as well
class CustomerDaoTest {
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerDao customerDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void genericSaveEntityValid() {
        Customer c = new Customer("testurl");
        Mockito.when(customerRepository.save(c)).thenReturn(c);
        Assertions.assertEquals(c, customerDao.genericSaveEntity(c));
    }

    @Test
    void genericFindByIdValid() {
        Customer c = new Customer("testurl");
        Mockito.when(customerRepository.findById((long)1)).thenReturn(Optional.of(c));
        Assertions.assertEquals(c, customerDao.genericFindById((long)1));
    }

    @Test
    void genericFindByIdNotFound() {
        Mockito.when(customerRepository.findById((long)1)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            customerDao.genericFindById((long)1);
        });
    }

    @Test
    void genericFindAllValid() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("urlA"));
        customers.add(new Customer("urlB"));
        Mockito.when(customerRepository.findAll()).thenReturn(customers);
        Assertions.assertEquals(customers, customerDao.genericFindAll());
    }
}