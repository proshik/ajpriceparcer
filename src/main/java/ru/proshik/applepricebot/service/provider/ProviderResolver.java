package ru.proshik.applepricebot.service.provider;


import org.springframework.stereotype.Component;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.repository.model.ProviderType;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProviderResolver {

    private Map<ProviderType, ScreeningProvider> PROVIDERS = new HashMap<>();

    public ProviderResolver() {
        PROVIDERS.put(ProviderType.AJ, new AjScreeningProvider());
        PROVIDERS.put(ProviderType.GSM_STORE, new GsmStoreScreeningProvider());
        PROVIDERS.put(ProviderType.ISTORE_SBP, new IStoreSpbScreeningProvider());
        PROVIDERS.put(ProviderType.CITI_LINK, new CitilinkSpbScreeningProvider());
    }

    public ScreeningProvider resolve(Provider provider) {
        ScreeningProvider screeningProvider = PROVIDERS.get(provider.getType());
        if (screeningProvider == null) {
            throw new IllegalArgumentException("Not found screeningProvider module for type=" + provider.getType());
        }

        return screeningProvider;
    }

}