package ru.proshik.applepriceparcer.provider;

import ru.proshik.applepriceparcer.screener.Screener;
import ru.proshik.applepriceparcer.screener.aj.AjScreener;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ScreenerProviderFactory {

    private static final List<Screener> SCREENERS = Arrays.asList(new AjScreener());

    public ScreenerProviderFactory() {
    }

    public List<Screener> list() {
        return SCREENERS;
    }

    public Screener findByTitle(String title) {
        return SCREENERS.stream()
                .filter(s -> title.toUpperCase().equals(s.getShop().getTitle().toUpperCase()))
                .findFirst()
                .orElse(null);
    }

}