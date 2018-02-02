package ru.proshik.applepriceparcer.model2.result;

import ru.proshik.applepriceparcer.model2.Shop;

import java.util.Set;


public class SubscriptionResult {

    private Set<Shop> addedSubscriptions;
    private Set<Shop> availableSubscriptons;

    public SubscriptionResult(Set<Shop> addedSubscriptions, Set<Shop> availableSubscriptons) {
        this.addedSubscriptions = addedSubscriptions;
        this.availableSubscriptons = availableSubscriptons;
    }

    public Set<Shop> getAddedSubscriptions() {
        return addedSubscriptions;
    }

    public Set<Shop> getAvailableSubscriptons() {
        return availableSubscriptons;
    }
}
