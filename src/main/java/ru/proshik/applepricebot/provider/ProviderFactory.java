package ru.proshik.applepricebot.provider;


import ru.proshik.applepricebot.provider.citilink.CitilinkSpbProvider;
import ru.proshik.applepricebot.storage.model.Shop;
import ru.proshik.applepricebot.provider.aj.AjProvider;
import ru.proshik.applepricebot.provider.gsmstore.GsmStoreProvider;
import ru.proshik.applepricebot.provider.istorespb.IStoreSpbProvider;

import java.util.*;

public class ProviderFactory {

    private Map<Shop, Provider> PROVIDERS = new HashMap<>();

    public ProviderFactory() {
        PROVIDERS.put(new Shop(AjProvider.TITLE, AjProvider.URL), new AjProvider());
        PROVIDERS.put(new Shop(GsmStoreProvider.TITLE, GsmStoreProvider.URL), new GsmStoreProvider());
        PROVIDERS.put(new Shop(IStoreSpbProvider.TITLE, IStoreSpbProvider.URL), new IStoreSpbProvider());
        PROVIDERS.put(new Shop(CitilinkSpbProvider.TITLE, CitilinkSpbProvider.URL), new CitilinkSpbProvider());
    }

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