package ru.proshik.applepricebot.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_seq_gen")
    @SequenceGenerator(name = "subscription_seq_gen", sequenceName = "subscription_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToOne
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    public Subscription() {
    }


}
