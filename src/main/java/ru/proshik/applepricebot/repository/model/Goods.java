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
@Table(name = "goods")
public class Goods {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "goods_seq_gen")
    @SequenceGenerator(name = "goods_seq_gen", sequenceName = "goods_seq")
    private Long id;

    @Column(name = "created_date", updatable = false, insertable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @Enumerated
    @Column(name = "type")
    private GoodsType goodsType;



}

