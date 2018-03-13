package ru.proshik.applepricebot.repository.model;

import org.hibernate.annotations.Immutable;
import ru.proshik.applepricebot.storage.model.ProductType;

import javax.persistence.*;

@Entity
@Immutable
@Table(name = "product_types")
public class ProductTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_type_seq_gen")
    @SequenceGenerator(name = "product_type_seq_gen", sequenceName = "product_type_seq")
    private Long id;

    @Column(name = "title", unique = true)
    private String title;

    @Enumerated
    @Column(name = "type")
    private ProductType productType;

    public ProductTypes() {
    }

    public ProductTypes(String title, ProductType productType) {
        this.title = title;
        this.productType = productType;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ru.proshik.applepricebot.storage.model.ProductType getProductType() {
        return productType;
    }
}
