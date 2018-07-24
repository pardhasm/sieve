package com.pardhasm.sieve.core.model;

public enum HttpType {
    HTTP("http"), HTTPS("https");
    private String value;

    HttpType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
