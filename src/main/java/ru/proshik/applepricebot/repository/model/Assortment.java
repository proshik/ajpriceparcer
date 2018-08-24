package ru.proshik.applepricebot.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "assortment")
public class Assortment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assortment_id_seq")
    @GenericGenerator(
            name = "assortment_id_seq",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(
                    name = SequenceStyleGenerator.SEQUENCE_PARAM,
                    value = "assortment_id_seq"))
    private Long id;

    @Column(name = "created_date", updatable = false, insertable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private ZonedDateTime createdDate;

    @Column(name = "fetch_date")
    private LocalDateTime fetchDate;

    private FetchType fetchType;

    @OneToOne
    @JoinColumn(name="provider_id")
    private Provider provider;

    @OneToMany(mappedBy = "assortment", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Product> products;

}
