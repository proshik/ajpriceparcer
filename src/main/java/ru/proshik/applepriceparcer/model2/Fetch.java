package ru.proshik.applepriceparcer.model2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Fetch implements Serializable {

    private LocalDateTime createdDate;
    private List<Assortment> assortments;

    public Fetch() {
    }

    public Fetch(LocalDateTime createdDate, List<Assortment> assortments) {
        this.createdDate = createdDate;
        this.assortments = assortments;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public List<Assortment> getAssortments() {
        return assortments;
    }
}
