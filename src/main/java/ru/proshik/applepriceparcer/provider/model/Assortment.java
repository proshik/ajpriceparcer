package ru.proshik.applepriceparcer.provider.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Assortment {

    private LocalDateTime createdDate;
    private List<Product> products;

    public Assortment(LocalDateTime createdDate, List<Product> products) {
        this.createdDate = createdDate;
        this.products = products;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public List<Product> getProducts() {
        return products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assortment that = (Assortment) o;
        return Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(products, that.products);
    }

    @Override
    public int hashCode() {

        return Objects.hash(createdDate, products);
    }
}
