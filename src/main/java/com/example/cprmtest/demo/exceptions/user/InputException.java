package com.example.cprmtest.demo.exceptions.user;
/* User readable input exception
 */
public class InputException extends RuntimeException{
    public InputException() {
        super("Sorry! Illegal input given.");
    }

    public InputException(String msg) {
        super(msg);
    }
}
