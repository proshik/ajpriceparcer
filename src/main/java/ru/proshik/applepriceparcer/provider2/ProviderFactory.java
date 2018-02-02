package ru.proshik.applepriceparcer.provider2;


import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.provider2.aj.AjProvider;
import ru.proshik.applepriceparcer.provider2.gsmstore.GsmStoreProvider;
import ru.proshik.applepriceparcer.provider2.istorespb.IStoreSpbProvider;

import java.util.*;

public class ProviderFactory {

    private static final Map<Shop, Provider> PROVIDERS = Map.of(
            new Shop(AjProvider.TITLE, AjProvider.URL), new AjProvider(),
            new Shop(GsmStoreProvider.TITLE, GsmStoreProvider.URL), new GsmStoreProvider(),
            new Shop(IStoreSpbProvider.TITLE, IStoreSpbProvider.URL), new IStoreSpbProvider());

    public Map<Shop, Provider> providers() {
        return PROVIDERS;
    }

    public List<Provider> getProviders() {
        return new ArrayList<>(PROVIDERS.values());
    }

    public Set<Shop> getShops() {
        return new HashSet<>(PROVIDERS.keySet());
    }

    public Provider findByShop(Shop shop) {
        return PROVIDERS.get(shop);
    }

    public Provider findByShopTitle(String title) {
        return PROVIDERS.keySet().stream()
                .filter(s -> title.toUpperCase().equals(s.getTitle().toUpperCase()))
                .findFirst()
                .map(PROVIDERS::get)
                .orElse(null);
    }

}