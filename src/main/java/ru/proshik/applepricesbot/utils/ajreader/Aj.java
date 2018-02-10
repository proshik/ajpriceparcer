package ru.proshik.applepricesbot.utils.ajreader;

import java.time.LocalDateTime;
import java.util.List;

public class Aj {

    private LocalDateTime createdDate;
    private List<AjAssortments> assortments;

    public Aj() {
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public List<AjAssortments> getAssortments() {
        return assortments;
    }
}
