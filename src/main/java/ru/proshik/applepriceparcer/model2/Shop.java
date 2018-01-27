package ru.proshik.applepriceparcer.model2;

import java.io.Serializable;
import java.util.List;

public class Shop implements Serializable {

    private String title;
    private String url;
    private List<Fetch> assortments;

    public Shop() {
    }

    public Shop(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public Shop(String title, String url, List<Fetch> assortments) {
        this.title = title;
        this.url = url;
        this.assortments = assortments;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public List<Fetch> getAssortments() {
        return assortments;
    }
}
