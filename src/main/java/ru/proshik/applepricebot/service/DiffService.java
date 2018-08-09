package ru.proshik.applepricebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.proshik.applepricebot.model.PriceProductDifferent;
import ru.proshik.applepricebot.repository.ProductRepository;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ShopType;
import ru.proshik.applepricebot.storage.model.ProductType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DiffService {

    @Autowired
    private ProductRepository productRepository;

    public Map<ShopType, Map<ProductType, List<PriceProductDifferent>>> buildPriceDifferent(Map<ShopType, List<Product>> products, LocalDateTime date) {
        Map<ShopType, Map<ProductType, List<PriceProductDifferent>>> result = new HashMap<>();

        Map<ShopType, List<Product>> prevGroupByShop =
                new HashMap<>();
//                productRepository.findByFetchDate(date).stream()
//                .collect(Collectors.groupingBy(Product::getShopType));

        for (Map.Entry<ShopType, List<Product>> entry : products.entrySet()) {

            Map<ProductType, List<Product>> prevProductGropByProductType = prevGroupByShop.get(entry.getKey()).stream()
                    .collect(Collectors.groupingBy(Product::getProductType));

            Map<ProductType, List<Product>> newProductsGroupByProductType = entry.getValue().stream()
                    .collect(Collectors.groupingBy(Product::getProductType));

            Map<ProductType, List<PriceProductDifferent>> productTypeResult = new HashMap<>();
            for (Map.Entry<ProductType, List<Product>> currProductEntry : newProductsGroupByProductType.entrySet()) {

                Map<String, List<Product>> previousGroupByTitle = prevProductGropByProductType.get(currProductEntry.getKey()).stream()
                        .collect(Collectors.groupingBy(Product::getTitle));

                List<PriceProductDifferent> priceProdDiffResult = new ArrayList<>();
                for (Product p : currProductEntry.getValue()) {
                    List<Product> findProductsByTitle = previousGroupByTitle.get(p.getTitle());
                    if (findProductsByTitle.isEmpty()) {
                        priceProdDiffResult.add(new PriceProductDifferent(p, null, p.getPrice()));
                        continue;
                    }

                    if (findProductsByTitle.size() > 1) {
                        Product prevProduct = findProductsByTitle.get(0);
                        if (!prevProduct.getPrice().equals(p.getPrice())) {
                            priceProdDiffResult.add(new PriceProductDifferent(p, prevProduct.getPrice(), p.getPrice()));
                        }
                    }
                }

                productTypeResult.put(currProductEntry.getKey(), priceProdDiffResult);

            }

            result.put(entry.getKey(), productTypeResult);
        }

        return result;
    }
}
