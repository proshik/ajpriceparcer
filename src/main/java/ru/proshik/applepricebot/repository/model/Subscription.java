package ru.proshik.applepricebot.repository.model;

import org.hibernate.annotations.GenericGenerator;
import ru.proshik.applepricebot.storage.model.ProductType;

import javax.persistence.*;

@Entity
@Table(name = "subscriptions")
@GenericGenerator(name = "subscriptions_id_generator", strategy = "sequence", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence", value = "subscriptions_id_seq")})
public class Subscription {

    @Id
    @GeneratedValue(generator = "subscriptions_id_generator")
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
