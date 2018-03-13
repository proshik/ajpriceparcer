package ru.proshik.applepricebot.repository.model;

public class ProductParameter {

    private String key;
    private String value;

    public ProductParameter() {
    }

    public ProductParameter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
