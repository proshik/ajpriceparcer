package ru.proshik.applepricebot.repository.model;

import java.util.Arrays;

public enum ProviderType {

    AJ("AJ.ru", "http://aj.ru", true),
    GSM_STORE("GSM Store", "http://gsm-store.ru", true),
    ISTORE_SBP("iStore-sbp", "http://istorespb.ru", false),
    CITI_LINK("Citilink", "http://citilink.ru", false);

    private final String title;
    private final String url;
    private final boolean enabled;

    ProviderType(String title, String url, boolean enabled) {
        this.title = title;
        this.url = url;
        this.enabled = enabled;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static ProviderType fromTitle(String title) {
        return Arrays.asList(values()).stream()
                .filter(shopType -> title.endsWith(shopType.title))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unexpected title value = " + title));
    }

}
