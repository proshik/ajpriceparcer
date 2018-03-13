package ru.proshik.applepricebot.repository.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "fetchs")
public class Fetch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fetch_seq_gen")
    @SequenceGenerator(name = "fetch_seq_gen", sequenceName = "fetch_seq")
    private Long id;

    @Column(name = "created_date", updatable = false, insertable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private ZonedDateTime createdDate;

    @Column(name = "fetch_date")
    private LocalDateTime fetchDate;

    @OneToOne
    @JoinColumn(name="shop_id")
    private Shop shop;

    @OneToMany(mappedBy = "fetch")
    private List<Product> products;

    public Fetch() {
    }

    public Fetch(ZonedDateTime createdDate, LocalDateTime fetchDate, Shop shop, List<Product> products) {
        this.createdDate = createdDate;
        this.fetchDate = fetchDate;
        this.shop = shop;
        this.products = products;
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

    public Shop getShop() {
        return shop;
    }

    public List<Product> getProducts() {
        return products;
    }
}
