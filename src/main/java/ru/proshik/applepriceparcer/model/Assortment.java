package ru.proshik.applepriceparcer.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Assortment implements Serializable {

    private LocalDateTime createdDate;
    private Map<ProductType, List<Product>> products = new HashMap<>();

    public Assortment() {
    }

    public Assortment(LocalDateTime createdDate, Map<ProductType, List<Product>> products) {
        this.createdDate = createdDate;
        this.products = products;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public Map<ProductType, List<Product>> getProducts() {
        return products;
    }

    public List<Product> byProductType(ProductType productType) {
        return products.get(productType);
    }
}
