package ru.proshik.applepricebot.service;

import org.springframework.stereotype.Component;
import ru.proshik.applepricebot.storage.model.ProductType;
import ru.proshik.applepricebot.storage.model.Shop;
import ru.proshik.applepricebot.provider.ProviderFactory;

import java.util.Set;
import java.util.stream.Stream;

@Component
public class ShopService {

    private ProviderFactory providerFactory = new ProviderFactory();

//    public ShopService(ProviderFactory providerFactory) {
//        this.providerFactory = providerFactory;
//    }

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
