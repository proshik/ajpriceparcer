package ru.proshik.applepriceparcer.provider;

import ru.proshik.applepriceparcer.provider.aj.AjProvider;

import java.util.Arrays;
import java.util.List;

public class ProviderFactory {

    private static final List<Provider> PROVIDERS = Arrays.asList(new AjProvider());

    public ProviderFactory() {
    }

    public List<Provider> list() {
        return PROVIDERS;
    }

    public Provider findByTitle(String title) {
        return PROVIDERS.stream()
                .filter(s -> title.toUpperCase().equals(s.getShop().getTitle().toUpperCase()))
                .findFirst()
                .orElse(null);
    }

}