package ru.proshik.applepricebot.storage.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserSubscriptions implements Serializable {

    private Set<Shop> shops = new HashSet<>();

    public UserSubscriptions() {
    }

    public UserSubscriptions(Set<Shop> shops) {
        this.shops = shops;
    }

    public Set<Shop> getShops() {
        return shops;
    }
}
