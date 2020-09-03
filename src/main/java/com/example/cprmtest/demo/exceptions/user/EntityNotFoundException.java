package com.example.cprmtest.demo.exceptions.user;
/* if certain entity is not found in db
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException() {
        super("Sorry! Could not find.");
    }

    public EntityNotFoundException(String entityName) {
        super("Sorry! Could not find " + entityName);
    }
}
