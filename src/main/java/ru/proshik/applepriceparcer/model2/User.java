package ru.proshik.applepriceparcer.model2;

import ru.proshik.applepriceparcer.model2.Shop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private List<Shop> subscriptions = new ArrayList<>();

    public User() {
    }

    public User(List<Shop> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Shop> getSubscriptions() {
        return subscriptions;
    }
}
