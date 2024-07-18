package com.gitlab.model;

public enum CurrencyCode {
    USD("R01235"),
    EURO("R01239"),
    CNY("R01375");

    private final String currencyCode;

    CurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCode() {
        return currencyCode;
    }
}
