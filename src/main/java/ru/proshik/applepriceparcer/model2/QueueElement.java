package ru.proshik.applepriceparcer.model2;

public class QueueElement {
    private String userId;
    private Shop shop;

    public QueueElement(String userId, Shop shop) {
        this.userId = userId;
        this.shop = shop;
    }

    public String getUserId() {
        return userId;
    }

    public Shop getShop() {
        return shop;
    }
}
