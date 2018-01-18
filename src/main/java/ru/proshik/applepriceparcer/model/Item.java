package ru.proshik.applepriceparcer.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public class Item implements Serializable {

    private String title;
    private BigDecimal price;
    private Map<String, String> parameters = new TreeMap<>();

    public Item() {
    }

    public Item(String title, BigDecimal price) {
        this.title = title;
        this.price = price;
    }

    public Item(String title, BigDecimal price, Map<String, String> parameters) {
        this.title = title;
        this.price = price;
        this.parameters = parameters;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }
}
