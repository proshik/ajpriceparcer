package ru.proshik.applepriceparcer;

import ru.proshik.applepriceparcer.model2.Fetch;

import java.util.Comparator;
import java.util.List;

public class FetchUtils {

    public static Fetch findLastFetch(List<Fetch> fetch) {
        return fetch.stream()
                .max(Comparator.comparing(Fetch::getCreatedDate))
                .orElseThrow(() -> new IllegalArgumentException("Must be at least one element"));
    }
}
