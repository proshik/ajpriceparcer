package ru.proshik.applepricebot.dto;

import ru.proshik.applepricebot.storage.model.Shop;

import java.util.List;

public class ChangeProductNotification {

    private String userId;
    private Shop shop;
    private List<DiffProducts> diffProducts;

    public ChangeProductNotification(String userId, Shop shop, List<DiffProducts> diffProducts) {
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
