package ru.proshik.applepricebot.repository.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    public void setUser(User user) {
        this.user = user;
    }
}
