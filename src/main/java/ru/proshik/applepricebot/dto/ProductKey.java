package ru.proshik.applepricebot.dto;

import ru.proshik.applepricebot.storage.model.ProductType;

import java.util.Objects;

public class ProductKey {

    private String title;
    private String description;
    private ProductType productType;

    public ProductKey(String title, String description, ProductType productType) {
        this.title = title;
        this.description = description;
        this.productType = productType;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public ProductType getProductType() {
        return productType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductKey that = (ProductKey) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                productType == that.productType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, description, productType);
    }
}
