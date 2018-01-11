package ru.proshik.applepriceparcer.console.command.model.goods;

import java.util.List;
import java.util.Objects;

public class Assortment {

    private String title;
    private String description;
    private List<Item> items;

    public Assortment() {
    }

    public Assortment(String title, String description, List<Item> items) {
        this.title = title;
        this.description = description;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assortment that = (Assortment) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, description, items);
    }
}
