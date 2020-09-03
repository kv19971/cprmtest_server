package com.example.cprmtest.demo.model.dto.enums;

public enum OptionType {
    PUT("PUT"),
    CALL("CALL");

    private String type;

    public String getType() {
        return type;
    }

    OptionType(String type) {
        this.type = type;
    }

}
