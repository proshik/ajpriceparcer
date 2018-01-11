package ru.proshik.applepriceparcer.provider.model;

public class Shop {

    private final String title;
    private final String url;

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
}
