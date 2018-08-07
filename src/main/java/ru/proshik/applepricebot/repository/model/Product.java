package ru.proshik.applepricebot.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import ru.proshik.applepricebot.storage.model.ProductType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_seq")
    @GenericGenerator(
            name = "product_id_seq",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(
                    name = SequenceStyleGenerator.SEQUENCE_PARAM,
                    value = "product_id_seq"))
    private Long id;

    @Column(name = "created_date", updatable = false, insertable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private ZonedDateTime createdDate;

    @Column(name = "data")
    private String data;

    @ManyToOne
    @JoinColumn(name = "assortment_id", nullable = false, updatable = false)
    private Assortment assortment;

}
