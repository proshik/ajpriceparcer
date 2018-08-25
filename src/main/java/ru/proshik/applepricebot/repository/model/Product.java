package ru.proshik.applepricebot.repository.model;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class),
        @TypeDef(name = "json-node", typeClass = JsonNodeStringType.class)})
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

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "price")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type")
    private ProductType productType;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private String parameters;

    @ManyToOne
    @JoinColumn(name = "assortment_id", nullable = false, updatable = false)
    private Assortment assortment;

    public void setAssortment(Assortment assortment) {
        this.assortment = assortment;
    }

}
