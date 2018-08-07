package ru.proshik.applepricebot.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "assortment")
public class Assortment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assortment_seq_gen")
    @SequenceGenerator(name = "assortment_seq_gen", sequenceName = "assortment_seq")
    private Long id;

    @Column(name = "created_date", updatable = false, insertable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private ZonedDateTime createdDate;

    @Column(name = "fetch_date")
    private LocalDateTime fetchDate;

    @OneToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToOne
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @OneToMany(mappedBy = "assortment")
    private List<Product> products;

}
