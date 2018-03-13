package ru.proshik.applepricebot.repository.model;

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

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "price")
    private BigDecimal price;

    @OneToOne
    @JoinColumn(name = "product_type_id")
    private ProductTypes productTypes;

    @ManyToOne
    @JoinColumn(name = "fetch_id", nullable = false, updatable = false)
    private Fetch fetch;

    @Column(name = "parameters")
    private String parameters;


    public Product() {
    }

    public Product(ZonedDateTime createdDate, String title, String description, Boolean available, BigDecimal price,
                   ProductTypes productTypes, Fetch fetch, String parameters) {
        this.createdDate = createdDate;
        this.title = title;
        this.description = description;
        this.available = available;
        this.price = price;
        this.productTypes = productTypes;
        this.fetch = fetch;
        this.parameters = parameters;
    }

    public Long getId() {
        return id;
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

    public ProductTypes getProductTypes() {
        return productTypes;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public Fetch getFetch() {
        return fetch;
    }

    public String getParameters() {
        return parameters;
    }
}
