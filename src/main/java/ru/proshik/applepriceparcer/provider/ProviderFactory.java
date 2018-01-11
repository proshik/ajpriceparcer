package ru.proshik.applepriceparcer.provider;

import ru.proshik.applepriceparcer.provider.screener.Screener;
import ru.proshik.applepriceparcer.provider.screener.aj.AjScreener;

import java.util.Arrays;
import java.util.List;

public class ProviderFactory {

    private static final List<Screener> SCREENERS = Arrays.asList(new AjScreener());

    public static List<Screener> list() {
        return SCREENERS;
    }

}