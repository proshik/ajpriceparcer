package ru.proshik.applepriceparcer.model;

import java.util.Objects;

public class Shop {

    private String title;
    private String url;

    public Shop() {
    }

    public Shop(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shop shop = (Shop) o;
        return Objects.equals(title, shop.title) &&
                Objects.equals(url, shop.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, url);
    }
}
