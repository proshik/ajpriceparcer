package ru.proshik.applepriceparcer.model;

import java.util.List;
import java.util.Objects;

public class Product {

    private String title;
    private String description;
    private List<Item> items;

    public Product() {
    }

    public Product(String title, String description, List<Item> items) {
        this.title = title;
        this.description = description;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Item> getItems() {
        return items;
    }

}
