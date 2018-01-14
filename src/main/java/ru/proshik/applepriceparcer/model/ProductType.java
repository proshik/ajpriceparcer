package ru.proshik.applepriceparcer.model;

public enum ProductType {

    IPHONE("iPhone"),
    IPAD("iPad"),
    IMAC("iMac"),
    MAC_MINI("Mac Mini"),
    MACBOOK_PRO("Macbook Pro"),
    MACBOOK_AIR("Macbook Air"),
    ACCESSORIES("Accessories"),
    OTHER("Other");

    private String value;

    ProductType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
