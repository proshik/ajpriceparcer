package ru.proshik.applepriceparcer.model2;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable {

    private Set<Shop> subscriptions = new HashSet<>();

    public User() {
    }

    public User(Set<Shop> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Set<Shop> getSubscriptions() {
        return subscriptions;
    }
}
