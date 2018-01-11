package ru.proshik.applepriceparcer.provider.model;

import java.util.List;
import java.util.Objects;

public class Product {

    private String title;
    private ProductType productType;
    private String description;
    private List<Item> items;

    public Product(String title, ProductType productType, String description, List<Item> items) {
        this.title = title;
        this.productType = productType;
        this.description = description;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public ProductType getProductType() {
        return productType;
    }

    public String getDescription() {
        return description;
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(title, product.title) &&
                productType == product.productType &&
                Objects.equals(description, product.description) &&
                Objects.equals(items, product.items);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, productType, description, items);
    }
}
