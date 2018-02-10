package ru.proshik.applepricebot.dto;

import java.time.LocalDateTime;
import java.util.List;

public class HistoryDiff {

    private LocalDateTime oldCreatedDate;
    private LocalDateTime newCreatedDAte;
    private List<DiffProducts> diff;

    public HistoryDiff(LocalDateTime oldCreatedDate, LocalDateTime newCreatedDAte, List<DiffProducts> diff) {
        this.oldCreatedDate = oldCreatedDate;
        this.newCreatedDAte = newCreatedDAte;
        this.diff = diff;
    }

    public LocalDateTime getOldCreatedDate() {
        return oldCreatedDate;
    }

    public LocalDateTime getNewCreatedDAte() {
        return newCreatedDAte;
    }

    public List<DiffProducts> getDiff() {
        return diff;
    }
}
