package ru.proshik.applepricebot.repository.model;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Immutable
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_seq_gen")
    @SequenceGenerator(name = "shop_seq_gen", sequenceName = "shop_seq")
    private Long id;

    @Column(name = "created_date", updatable = false, insertable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private ZonedDateTime createdDate;

    @Column(name = "title")
    private String title;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;

    public Shop() {
    }

    public Shop(ZonedDateTime createdDate, String title, String url, String description) {
        this.createdDate = createdDate;
        this.title = title;
        this.url = url;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }
}
