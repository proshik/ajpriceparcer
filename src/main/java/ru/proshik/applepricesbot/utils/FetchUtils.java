package ru.proshik.applepricesbot.utils;

import ru.proshik.applepricesbot.storage.model.Fetch;

import java.util.Comparator;
import java.util.List;

public class FetchUtils {

    public static Fetch findLastFetch(List<Fetch> fetch) {
        return fetch.stream()
                .max(Comparator.comparing(Fetch::getCreatedDate))
                .orElseThrow(() -> new IllegalArgumentException("Must be at least one element"));
    }
}
