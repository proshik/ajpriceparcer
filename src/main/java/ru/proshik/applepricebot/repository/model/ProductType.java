package ru.proshik.applepricebot.repository.model;

import java.io.Serializable;
import java.util.Arrays;

public enum ProductType implements Serializable {
    /*
    iPhone
     */
    IPHONE_SE("iPhone SE"),
    IPHONE_6("iPhone 6"),
    IPHONE_6_PLUS("iPhone 6 Plus"),
    IPHONE_6S("iPhone 6S"),
    IPHONE_6S_PLUS("iPhone 6S Plus"),
    IPHONE_7("iPhone 7"),
    IPHONE_7_PLUS("iPhone 7 Plus"),
    IPHONE_8("iPhone 8"),
    IPHONE_8_PLUS("iPhone 8 Plus"),
    IPHONE_X("iPhone X"),
    IPHONE_XS("iPhone XS"),
    IPHONE_XS_MAX("iPhone XS Max"),
    IPHONE_XR("iPhone XR"),

    /*
    iMac
     */
    IMAC_21_5("iMac 21.5"),

    IMAC_27("iMac 27"),

    IMAC_PRO_27("iMac Pro"),

    /*
    Macbook Pro
     */
    MACBOOK_PRO_2017("MacBook Pro 2017"),

    MACBOOK_PRO_2016("MacBook Pro 2016");

    private String value;

    ProductType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProductType fromValue(String value) {
        return Arrays.stream(values())
                .filter(pro -> pro.getValue().toUpperCase().equals(value.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unexpected ProductTypes value=" + value));
    }
}
