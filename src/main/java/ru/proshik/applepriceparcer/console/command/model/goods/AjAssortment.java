package ru.proshik.applepriceparcer.console.command.model.goods;

import java.time.LocalDateTime;
import java.util.List;

public class AjAssortment {

    private LocalDateTime createdDate;
    private List<Assortment> assortments;

    public AjAssortment() {
    }

    public AjAssortment(LocalDateTime createdDate, List<Assortment> assortments) {
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
