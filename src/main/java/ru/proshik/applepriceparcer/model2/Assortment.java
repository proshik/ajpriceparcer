package ru.proshik.applepriceparcer.model2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Assortment implements Serializable {

    private String title;
    private String description;
    private AssortmentType assortmentType;
    private List<Product> products;

    public Assortment() {
    }

    public Assortment(String title, String description, AssortmentType assortmentType, List<Product> products) {
        this.title = title;
        this.description = description;
        this.assortmentType = assortmentType;
        this.products = products;
    }

    public String getTitle() {
        return title;
    }

    public AssortmentType getAssortmentType() {
        return assortmentType;
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getDescription() {
        return description;
    }
}
