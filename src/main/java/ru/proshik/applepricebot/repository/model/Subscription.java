package ru.proshik.applepricebot.repository.model;

import javax.persistence.*;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_seq_gen")
    @SequenceGenerator(name = "subscription_seq_gen", sequenceName = "subscription_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name="shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "product_type_id")
    private ProductTypes productTypes;

    public Subscription() {
    }

    public Subscription(Shop shop, ProductTypes productTypes) {
        this.shop = shop;
        this.productTypes = productTypes;
    }

    public Long getId() {
        return id;
    }

    public Shop getShop() {
        return shop;
    }

    public ProductTypes getProductTypes() {
        return productTypes;
    }
}
