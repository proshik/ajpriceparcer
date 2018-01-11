package ru.proshik.applepriceparcer.provider.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Item {

    private String title;
    private BigDecimal price;

    public Item() {
    }

    public Item(String title, BigDecimal price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(title, item.title) &&
                Objects.equals(price, item.price);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, price);
    }
}
