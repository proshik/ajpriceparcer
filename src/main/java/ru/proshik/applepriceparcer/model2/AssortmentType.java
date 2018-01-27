package ru.proshik.applepriceparcer.model2;

import java.util.Arrays;

public enum AssortmentType {

    IPHONE("iPhone"),
    IPAD("iPad"),
    IPOD("iPod"),
    MACBOOK("MacBook"),
    MACBOOK_AIR("Macbook Air"),
    MACBOOK_PRO("Macbook Pro"),
    IMAC("iMac"),
    MAC_MINI("Mac Mini"),
    OTHER("Other");

    private String value;

    AssortmentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AssortmentType fromValue(String value) {
        return Arrays.stream(values())
                .filter(a -> a.getValue().toUpperCase().equals(value.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unexpected AssortmentType value=" + value));
    }
}
