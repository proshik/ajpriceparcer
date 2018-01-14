package ru.proshik.applepriceparcer.model;

public class SelectedProduct {

    private String shopTitle;
    private String productType;


    public SelectedProduct() {
    }

    public SelectedProduct(String shopTitle) {
        this.shopTitle = shopTitle;
    }

    public SelectedProduct(String shopTitle, String productType) {
        this.shopTitle = shopTitle;
        this.productType = productType;
    }

    public String getShopTitle() {
        return shopTitle;
    }

    public String getProductType() {
        return productType;
    }

    public void setShopTitle(String shopTitle) {
        this.shopTitle = shopTitle;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public boolean emptyShop() {
        return shopTitle == null;
    }

    public boolean emptyProductType() {
        return productType == null;
    }
}
