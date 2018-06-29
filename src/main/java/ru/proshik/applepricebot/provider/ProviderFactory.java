package ru.proshik.applepricebot.provider;


import org.springframework.stereotype.Component;
import ru.proshik.applepricebot.provider.aj.AjProvider;
import ru.proshik.applepricebot.provider.citilink.CitilinkSpbProvider;
import ru.proshik.applepricebot.provider.gsmstore.GsmStoreProvider;
import ru.proshik.applepricebot.provider.istorespb.IStoreSpbProvider;
import ru.proshik.applepricebot.repository.model.ShopType;
import ru.proshik.applepricebot.storage.model.Shop;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ProviderFactory {

    private Map<ShopType, Provider> PROVIDERS = new HashMap<>();

    public ProviderFactory() {
        PROVIDERS.put(ShopType.AJ, new AjProvider());
        PROVIDERS.put(ShopType.GSM_STORE, new GsmStoreProvider());
        PROVIDERS.put(ShopType.ISTORE_SBP, new IStoreSpbProvider());
        PROVIDERS.put(ShopType.CITI_LINK, new CitilinkSpbProvider());
    }

    public Map<ShopType, Provider> providers() {
        return PROVIDERS;
    }

    public Set<Shop> getShops() {
        // TODO: 26.06.2018  
        return Collections.emptySet();
//        return new HashSet<>(PROVIDERS.keySet());
    }

}