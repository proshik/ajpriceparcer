package ru.proshik.applepriceparcer.model2;

import java.util.List;

public class QueueElement {
    private String userId;
    private Shop shop;
    private List<DiffProducts> diffProducts;

    public QueueElement(String userId, Shop shop, List<DiffProducts> diffProducts) {
        this.userId = userId;
        this.shop = shop;
        this.diffProducts = diffProducts;
    }

    public String getUserId() {
        return userId;
    }

    public Shop getShop() {
        return shop;
    }

    public List<DiffProducts> getDiffProducts() {
        return diffProducts;
    }
}
