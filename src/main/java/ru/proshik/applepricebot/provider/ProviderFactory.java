package ru.proshik.applepricebot.provider;


import org.springframework.stereotype.Component;
import ru.proshik.applepricebot.model.ProviderInfo;
import ru.proshik.applepricebot.provider.aj.AjProvider;
import ru.proshik.applepricebot.provider.citilink.CitilinkSpbProvider;
import ru.proshik.applepricebot.provider.gsmstore.GsmStoreProvider;
import ru.proshik.applepricebot.provider.istorespb.IStoreSpbProvider;
import ru.proshik.applepricebot.repository.model.ShopType;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProviderFactory {

    private Map<ProviderInfo, Provider> PROVIDERS = new HashMap<>();

    public ProviderFactory() {
        PROVIDERS.put(new ProviderInfo(ShopType.AJ.getTitle(), ShopType.AJ.getUrl(), ShopType.AJ.isEnabled()), new AjProvider());
        PROVIDERS.put(new ProviderInfo(ShopType.GSM_STORE.getTitle(), ShopType.GSM_STORE.getUrl(),ShopType.GSM_STORE.isEnabled()), new GsmStoreProvider());
        PROVIDERS.put(new ProviderInfo(ShopType.ISTORE_SBP.getTitle(), ShopType.ISTORE_SBP.getUrl(), ShopType.ISTORE_SBP.isEnabled()), new IStoreSpbProvider());
        PROVIDERS.put(new ProviderInfo(ShopType.CITI_LINK.getTitle(), ShopType.CITI_LINK.getUrl(), ShopType.CITI_LINK.isEnabled()), new CitilinkSpbProvider());
    }

    public Map<ProviderInfo, Provider> providers() {
        return PROVIDERS;
    }

}