package ru.proshik.applepriceparcer.ajreader;

import java.util.List;

public class AjAssortments {

    private String title;
    private String description;
    private List<AjItem> items;

    public AjAssortments() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<AjItem> getItems() {
        return items;
    }
}
