package ru.proshik.applepricebot.utils;

import ru.proshik.applepricebot.storage.model.Fetch;

import java.util.Comparator;
import java.util.List;

public class FetchUtils {

    public static Fetch findLastFetch(List<Fetch> fetch) {
        return fetch.stream()
                .max(Comparator.comparing(Fetch::getCreatedDate))
                .orElseThrow(() -> new IllegalArgumentException("Must be at least one element"));
    }
}
