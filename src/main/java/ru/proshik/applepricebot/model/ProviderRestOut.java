package ru.proshik.applepricebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.proshik.applepricebot.repository.model.ProviderType;

@Getter
@Builder
@AllArgsConstructor
public class ProviderRestOut {

    private Long id;
    private String title;
    private String url;
    private ProviderType providerType;
    private boolean enabled;

}
