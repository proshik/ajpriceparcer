package ru.proshik.applepricebot.repository.model;

public enum ShopType {

    AJ("http://aj.ru", true),
    GSM_STORE("http://gsm-store.ru", true),
    ISTORE_SBP("http://istorespb.ru", false),
    CITI_LINK("http://citilink.ru", false);

    private final String url;
    private final boolean enabled;

    ShopType(String url, boolean enabled) {
        this.url = url;
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
