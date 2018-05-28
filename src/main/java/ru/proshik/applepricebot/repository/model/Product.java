package ru.proshik.applepricebot.repository.model;

import ru.proshik.applepricebot.storage.model.ProductType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq_gen")
    @SequenceGenerator(name = "product_seq_gen", sequenceName = "product_seq")
    private Long id;

    @Column(name = "created_date", updatable = false, insertable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private ZonedDateTime createdDate;

    @Column(name = "fetch_date", updatable = false)
    private ZonedDateTime fetchDate;

    @Column(name = "shopType")
    private ShopType shopType;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "productType")
    private ProductType productType;

    @Column(name = "parameters")
    private String parameters;


    public Product() {
    }

    public Product(ZonedDateTime fetchDate, ShopType shopType, String title,
                   String description, Boolean available, BigDecimal price, ProductType productType, String parameters) {
        this.createdDate = createdDate;
        this.fetchDate = fetchDate;
        this.shopType = shopType;
        this.title = title;
        this.description = description;
        this.available = available;
        this.price = price;
        this.productType = productType;
        this.parameters = parameters;
    }

    public Long getId() {
        return id;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public ZonedDateTime getFetchDate() {
        return fetchDate;
    }

    public ShopType getShopType() {
        return shopType;
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

    public ProductType getProductType() {
        return productType;
    }

    public String getParameters() {
        return parameters;
    }
}
