package com.example.cprmtest.demo.exceptions.user;

public class BadParametersException extends InputException {
    public BadParametersException() {
        super("Sorry! Bad parameters given");
    }

    public BadParametersException(String msg) {
        super(msg);
    }
}
