package ru.proshik.applepricebot.service.provider;


import org.springframework.stereotype.Component;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.repository.model.ProviderType;
import ru.proshik.applepricebot.service.provider.aj.AjScreening;
import ru.proshik.applepricebot.service.provider.citilink.CitilinkSpbScreening;
import ru.proshik.applepricebot.service.provider.gsmstore.GsmStoreScreening;
import ru.proshik.applepricebot.service.provider.istorespb.IStoreSpbScreening;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProviderResolver {

    private Map<ProviderType, Screening> PROVIDERS = new HashMap<>();

    public ProviderResolver() {
        PROVIDERS.put(ProviderType.AJ, new AjScreening());
        PROVIDERS.put(ProviderType.GSM_STORE, new GsmStoreScreening());
        PROVIDERS.put(ProviderType.ISTORE_SBP, new IStoreSpbScreening());
        PROVIDERS.put(ProviderType.CITI_LINK, new CitilinkSpbScreening());
    }

    public Screening resolve(Provider provider) {
        Screening screening = PROVIDERS.get(provider.getType());
        if (screening == null) {
            throw new IllegalArgumentException("Not found screening module for type=" + provider.getType());
        }

        return screening;
    }

}