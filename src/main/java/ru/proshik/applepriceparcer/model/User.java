package ru.proshik.applepriceparcer.model;

import java.util.List;

public class User {

    private List<Shop> subscriptions;

    public User() {
    }

    public User(List<Shop> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Shop> getSubscriptions() {
        return subscriptions;
    }
}
