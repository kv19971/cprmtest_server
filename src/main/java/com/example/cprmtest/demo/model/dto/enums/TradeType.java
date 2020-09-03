package com.example.cprmtest.demo.model.dto.enums;

public enum TradeType {
    SHORT("SHORT"),
    LONG("LONG");
    private String type;

    public String getType() {
        return type;
    }
    TradeType(String type) {
        this.type = type;
    }
}
