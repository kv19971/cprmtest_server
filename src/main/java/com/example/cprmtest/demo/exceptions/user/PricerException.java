package com.example.cprmtest.demo.exceptions.user;

public class PricerException extends BadParametersException {
    public PricerException() {
        super("Sorry! Bad parameters given to pricer");
    }

    public PricerException(String msg) {
        super(msg);
    }
}
