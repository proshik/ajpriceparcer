package ru.proshik.applepricebot.service.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.proshik.applepricebot.model.PriceProductDifferent;
import ru.proshik.applepricebot.repository.SubscriptionRepository;
import ru.proshik.applepricebot.repository.UserRepository;
import ru.proshik.applepricebot.repository.model.ShopType;
import ru.proshik.applepricebot.storage.model.ProductType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public void buildEventNotification(Map<ShopType, Map<ProductType, List<PriceProductDifferent>>> different) {

        Set<ShopProductPair> shopProductPairs = new HashSet<>();
        for (Map.Entry<ShopType, Map<ProductType, List<PriceProductDifferent>>> entry : different.entrySet()) {
            shopProductPairs.addAll(entry.getValue().entrySet().stream()
                    .map(productTypeListEntry -> new ShopProductPair(entry.getKey(), productTypeListEntry.getKey()))
                    .collect(Collectors.toList()));
        }

    }


    class ShopProductPair {

        private ShopType shopType;
        private ProductType productType;

        public ShopProductPair(ShopType shopType, ProductType productType) {
            this.shopType = shopType;
            this.productType = productType;
        }
    }

}
