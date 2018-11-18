package ru.proshik.applepricebot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.proshik.applepricebot.repository.model.ProviderType;
import ru.proshik.applepricebot.service.provider.ScreeningProvider;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProviderConfig {

    @Autowired
    private ScreeningProvider ajScreeningProvider;

    @Autowired
    private ScreeningProvider gsmStoreScreeningProvider;

    @Bean
    public Map<ProviderType, ScreeningProvider> screeningProviders() {
        Map<ProviderType, ScreeningProvider> providers = new HashMap<>();

        providers.put(ProviderType.AJ, ajScreeningProvider);
        providers.put(ProviderType.GSM_STORE, gsmStoreScreeningProvider);

        return providers;
    }

}
