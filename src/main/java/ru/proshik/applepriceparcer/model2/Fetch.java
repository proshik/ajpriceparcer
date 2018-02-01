package ru.proshik.applepriceparcer.model2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Fetch implements Serializable {

    private LocalDateTime createdDate;
    private List<Product> products;

    public Fetch() {
    }

    public Fetch(LocalDateTime createdDate, List<Product> products) {
        this.createdDate = createdDate;
        this.products = products;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public List<Product> getProducts() {
        return products;
    }
}
