package ru.proshik.applepriceparcer.provider;

import ru.proshik.applepriceparcer.provider.screener.Screener;
import ru.proshik.applepriceparcer.provider.screener.aj.AjScreener;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProviderFactory {

    private static final List<Screener> SCREENERS = Arrays.asList(new AjScreener());

    public static List<Screener> list() {
        return SCREENERS;
    }

    public static Optional<Screener> findByTitle(String title) {
        return SCREENERS.stream()
                .filter(s -> title.toUpperCase().equals(s.supplier().getTitle().toUpperCase()))
                .findFirst();
    }

}