package ru.proshik.applepricesbot.service;

import ru.proshik.applepricesbot.storage.model.ProductType;
import ru.proshik.applepricesbot.storage.model.Shop;
import ru.proshik.applepricesbot.provider.ProviderFactory;

import java.util.Set;
import java.util.stream.Stream;

public class ShopService {

    private ProviderFactory providerFactory;

    public ShopService(ProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }

    public Set<Shop> getShops() {
        return providerFactory.getShops();
    }

    public Shop findShop(String shopTitle) {
        return providerFactory.getShops().stream()
                .filter(shop -> shop.getTitle().toUpperCase().equals(shopTitle.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    public ProductType findProductType(String productTypeTitle) {
        return Stream.of(ProductType.values())
                .filter(pt -> pt.getValue().replace(" ", "").toUpperCase().equals(productTypeTitle.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

}
