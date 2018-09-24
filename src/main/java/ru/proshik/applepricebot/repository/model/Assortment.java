package ru.proshik.applepricebot.repository.model;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@EqualsAndHashCode(of = {"id"})
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

    @Enumerated(value = EnumType.STRING)
    @Column(name = "fetch_type")
    private FetchType fetchType;

    @OneToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @OneToMany(mappedBy = "assortment", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Product> products = new ArrayList<>();

}
