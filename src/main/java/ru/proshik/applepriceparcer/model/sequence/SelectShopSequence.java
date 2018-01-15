package ru.proshik.applepriceparcer.model.sequence;

import ru.proshik.applepriceparcer.model.ProductType;
import ru.proshik.applepriceparcer.model.Shop;

public class SelectShopSequence {

    private Shop shop;
    private ProductType productType;

    public SelectShopSequence() {
    }

    public SelectShopSequence(Shop shop) {
        this.shop = shop;
    }

    public SelectShopSequence(Shop shop, ProductType productType) {
        this.shop = shop;
        this.productType = productType;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public boolean emptyShop() {
        return shop == null;
    }

    public boolean emptyProductType() {
        return productType == null;
    }

}
