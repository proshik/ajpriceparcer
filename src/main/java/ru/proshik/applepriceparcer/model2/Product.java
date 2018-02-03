package ru.proshik.applepriceparcer.model2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Product implements Serializable {

    private String title;
    private String description;
    private Boolean available;
    private BigDecimal price;
    private AssortmentType assortmentType;
    private ProductType productType;
    private Map<String, String> parameters = new TreeMap<>();

    public Product() {
    }

    public Product(String title, String description, Boolean available, BigDecimal price, AssortmentType assortmentType,
                   ProductType productType) {
        this.title = title;
        this.description = description;
        this.available = available;
        this.price = price;
        this.assortmentType = assortmentType;
        this.productType = productType;
    }

    public Product(String title, String description, Boolean available, BigDecimal price, AssortmentType assortmentType,
                   ProductType productType, Map<String, String> parameters) {
        this.title = title;
        this.description = description;
        this.available = available;
        this.price = price;
        this.assortmentType = assortmentType;
        this.productType = productType;
        this.parameters = parameters;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public AssortmentType getAssortmentType() {
        return assortmentType;
    }

    public ProductType getProductType() {
        return productType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(title, product.title) &&
                Objects.equals(description, product.description) &&
                Objects.equals(available, product.available) &&
                Objects.equals(price, product.price) &&
                assortmentType == product.assortmentType &&
                productType == product.productType &&
                Objects.equals(parameters, product.parameters);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, description, available, price, assortmentType, productType, parameters);
    }

    @Override
    public String toString() {
        return "Product{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", price=" + price +
                ", assortmentType=" + assortmentType +
                ", productType=" + productType +
                ", parameters=" + parameters +
                '}';
    }
}
