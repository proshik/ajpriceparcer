package ru.proshik.applepricebot.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Immutable
@Table(name = "shop")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_seq_gen")
    @SequenceGenerator(name = "shop_seq_gen", sequenceName = "shop_seq")
    private Long id;

    @Column(name = "created_date", updatable = false, insertable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @Column(name = "type")
    private ShopType type;

    @Column(name = "url")
    private String url;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "description")
    private String description;

}
