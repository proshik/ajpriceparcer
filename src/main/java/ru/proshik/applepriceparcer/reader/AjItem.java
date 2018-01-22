package ru.proshik.applepriceparcer.reader;

import java.math.BigDecimal;

public class AjItem {

    private String title;
    private BigDecimal price;

    public AjItem() {
    }

    public AjItem(String title, BigDecimal price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
