package ru.proshik.applepricebot.repository.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import ru.proshik.applepricebot.storage.model.ProductType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "products",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fetch_date", "shop_type", "product_type", "title"}))
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_id_seq")
    @GenericGenerator(
            name = "products_id_seq",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(
                    name = SequenceStyleGenerator.SEQUENCE_PARAM,
                    value = "products_id_seq"))
    private Long id;

    @Column(name = "created_date", updatable = false, insertable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private ZonedDateTime createdDate;

    @Column(name = "fetch_date", updatable = false)
    private LocalDateTime fetchDate;

    @Column(name = "shop_type")
    private ShopType shopType;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "product_type")
    private ProductType productType;

    @Column(name = "parameters")
    private String parameters;

    public Product() {
    }

    public Product(ZonedDateTime createdDate, LocalDateTime fetchDate, ShopType shopType, String title,
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

    public LocalDateTime getFetchDate() {
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
