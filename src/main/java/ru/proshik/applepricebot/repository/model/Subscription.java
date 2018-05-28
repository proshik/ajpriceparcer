package ru.proshik.applepricebot.repository.model;

import ru.proshik.applepricebot.storage.model.ProductType;

import javax.persistence.*;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_seq_gen")
    @SequenceGenerator(name = "subscription_seq_gen", sequenceName = "subscription_seq")
    private Long id;

    @Column(name = "shopType")
    private ShopType shopType;

    @Column(name = "productType")
    private ProductType productType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    public Subscription() {
    }

    public Subscription(ShopType shopType, ProductType productType) {
        this.shopType = shopType;
        this.productType = productType;
    }

    public Long getId() {
        return id;
    }

    public ShopType getShopType() {
        return shopType;
    }

    public ProductType getProductType() {
        return productType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
