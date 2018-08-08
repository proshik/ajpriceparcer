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

        Map<ShopType, List<Product>> prevGroupByShop = productRepository.findByFetchDate(date).stream()
                .collect(Collectors.groupingBy(Product::getShopType));

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

//    public List<HistoryDiff> historyDiff(List<Fetch> fetches, Goods productType) {
//        List<HistoryDiff> result = new ArrayList<>();
//
//        List<Fetch> limitedFetches = fetches.stream()
//                .sorted((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()))
//                .limit(3)
//                .collect(Collectors.toList());
//        for (int i = 0; i < limitedFetches.size() - 1; i++) {
//            List<DiffProducts> diff = findDiff(limitedFetches.get(i + 1), limitedFetches.get(i), productType);
//            if (diff.isEmpty()) {
//                continue;
//            }
//            result.add(new HistoryDiff(limitedFetches.get(i + 1).getCreatedDate(), limitedFetches.get(i).getCreatedDate(), diff));
//        }
//
//        return result;
//    }
//
//    public List<DiffProducts> findDiff(Fetch oldFetch, Fetch newFetch, Goods productType) {
//        List<DiffProducts> diff = new ArrayList<>();
//
//        Map<ProductKey, List<Product>> groupNewFetch = newFetch.getProducts().stream()
//                .collect(Collectors.groupingBy(o -> new ProductKey(o.getTitle(), o.getDescription(), o.getProductType())));
//        Map<ProductKey, List<Product>> groupLastFetch = oldFetch.getProducts().stream()
//                .collect(Collectors.groupingBy(o -> new ProductKey(o.getTitle(), o.getDescription(), o.getProductType())));
//
//        List<Product> withoutDiff = new ArrayList<>();
//
//        for (Map.Entry<ProductKey, List<Product>> newEntry : groupNewFetch.entrySet()) {
//            if (productType != null && newEntry.getKey().getProductType() != productType) {
//                continue;
//            }
//            List<Product> oldProducts = groupLastFetch.get(newEntry.getKey());
//            if (oldProducts != null) {
//                List<Product> newProducts = newEntry.getValue();
//                newProducts.sort(Comparator.comparing(Product::getPrice));
//                oldProducts.sort(Comparator.comparing(Product::getPrice));
//
//                if (newProducts.size() == oldProducts.size()) {
//                    for (int i = 0; i < newProducts.size(); i++) {
//                        if (!oldProducts.get(i).getPrice().equals(newProducts.get(i).getPrice())
//                                || (oldProducts.get(i).getAvailable() != null && newProducts.get(i).getAvailable() != null
//                                && (!oldProducts.get(i).getAvailable().equals(newProducts.get(i).getAvailable())))) {
//                            diff.add(new DiffProducts(oldProducts.get(i), newProducts.get(i)));
//                        } else {
//                            withoutDiff.add(oldProducts.get(i));
//                        }
//                    }
//                } else {
//                    List<Product> forAdded = new ArrayList<>();
//                    if (newProducts.size() > oldProducts.size()) {
//                        newProducts.retainAll(oldProducts);
//                        forAdded.addAll(newProducts);
//                    } else {
//                        oldProducts.retainAll(newProducts);
//                        forAdded.addAll(oldProducts);
//                    }
//                    for (Product p : forAdded) {
//                        diff.add(new DiffProducts(null, p));
//                    }
//                }
//            } else {
//                for (Product p : newEntry.getValue()) {
//                    diff.add(new DiffProducts(null, p));
//                }
//            }
//        }

//        if (buildPriceDifferent.size() > 0) {
//            List<Product> old = oldFetch.getProducts();
//
//            List<Product> olders = buildPriceDifferent.stream()
//                    .filter(diffProducts -> diffProducts.getOldProductDesc() != null)
//                    .map(DiffProducts::getOldProductDesc)
//                    .collect(Collectors.toList());
//
//            old.removeAll(olders);
//            old.removeAll(withoutDiff);
//
//            if (old.size() > 0) {
//                for (Product p : old) {
//                    buildPriceDifferent.add(new DiffProducts(p, null));
//                }
//            }
//        }

//        return diff;
//    }

}
