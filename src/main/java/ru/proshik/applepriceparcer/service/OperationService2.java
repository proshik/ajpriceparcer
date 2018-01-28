package ru.proshik.applepriceparcer.service;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.exception.ServiceLayerException;
import ru.proshik.applepriceparcer.model2.Assortment;
import ru.proshik.applepriceparcer.model2.Product;
import ru.proshik.applepriceparcer.model2.ProductType;
import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.provider2.Provider;
import ru.proshik.applepriceparcer.provider2.ProviderFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class OperationService2 {

    private static final Logger LOG = Logger.getLogger(OperationService2.class);

    private static final int HISTORY_LIMIT = 5;

    private ProviderFactory providerFactory;
    private AssortmentService assortmentService;
    private UserService userService;

    public OperationService2(ProviderFactory providerFactory,
                             AssortmentService assortmentService,
                             UserService userService) {
        this.providerFactory = providerFactory;
        this.assortmentService = assortmentService;
        this.userService = userService;
    }

//    public Shop findShopByTitle(String title) {
//        Provider provider = providerFactory.list().stream()
//                .filter(p -> p.getShop().getTitle().toUpperCase().equals(title.toUpperCase()))
//                .findFirst()
//                .orElse(null);
//
//        if (provider != null) {
//            return provider.getShop();
//        } else {
//            return null;
//        }
//    }
//
//    public List<Shop> selectAvailableShops() {
//        return providerFactory.list().stream()
//                .map(Provider::getShop)
//                .sorted()
//                .collect(Collectors.toList());
//    }

//    public List<Provider> selectAllProviders() {
//        return providerFactory.list();
//    }
//
//    public List<ProductType> selectUniqueProductTypes(Shop shop) {
//        try {
//            List<Assortment> assortments = assortmentService.getAssortments(shop);
//
//            return assortments.stream()
//                    .flatMap(assortment -> assortment.getProducts().keySet().stream())
//                    .distinct()
//                    .collect(Collectors.toList());
//        } catch (DatabaseException e) {
//            LOG.error("Error on select unique product types", e);
//        }
//
//        return Collections.emptyList();
//    }

//    public Assortment priceAssortment(Shop shop, ProductType productType) throws ServiceLayerException {
//
//        List<Assortment> assortments = assortmentService.getAssortments(shop);
//        if (assortments.isEmpty()) {
//            return null;
//        }
//
//        return findLastAssortment(assortments);
//    }
//
//    public List<Assortment> historyAssortments(Shop shop, ProductType productType) throws DatabaseException {
//        Provider provider = providerFactory.findByShop(shop);
//        if (provider != null) {
//            List<Assortment> assortments = assortmentService.getAssortments(shop);
//
//            List<History> result = new ArrayList<>();
//
//            //temporary decision
//            if (assortments.stream().flatMap(a -> a.getProducts().keySet().stream().filter(pt -> productType == pt)).count() > 1) {
//                for (int i = 0; i < assortments.size() - 1; i++) {
//                    History history = historyChanges(assortments.get(i), assortments.get(i + 1), productType);
//                    result.add(history);
//                }
//            }
//
//            if (assortments.stream().flatMap(a -> a.getProducts().keySet().stream().filter(pt -> productType == pt)).count() > 1) {
//                return assortments.stream()
//                        .sorted((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()))
//                        .limit(HISTORY_LIMIT)
//                        .collect(Collectors.toList());
//            } else {
//                return Collections.emptyList();
//            }
//        } else {
//            return Collections.emptyList();
//        }
//    }
//
//    public Pair<List<Shop>, List<Shop>> userSubscriptions(String chatId) throws DatabaseException {
//        List<Shop> availableShops = providerFactory.list().stream()
//                .map(Provider::getShop)
//                .collect(Collectors.toList());
//
//        List<Shop> userSubscriptions = userService.userSubscriptions(chatId);
//
//        availableShops.removeAll(new ArrayList<>(userSubscriptions));
//
//        return new ImmutablePair<>(userSubscriptions, availableShops);
//    }
//
//    public void updateUserSubscriptions(String chatId, Shop shop) throws DatabaseException {
//        userService.addSubscription(chatId, shop);
//    }
//
//    // TODO: 18.01.2018
//    public void cancelSubscriptions(String chatId, Shop shop) {
//
//    }


//    /**
//     * Data: 2017-01-01
//     * Iphone X
//     * Iphone 64 Gb:
//     * Old price: 77000; New Price: 70000
//     * ...
//     */
//    private History historyChanges(Assortment firstA, Assortment secondA, ProductType productType) {
//        Map<String, Map<String, Prices>> productResultData = new TreeMap<>();
//
//        for (Map.Entry<ProductType, List<Product>> entry : firstA.getProducts().entrySet()) {
//            if (entry.getKey().equals(productType)) {
//                Map<String, Product> productsByTitle = secondA.byProductType(entry.getKey()).stream()
//                        .collect(Collectors.toMap(Product::getTitle, o -> o));
//
//                for (Product p1 : entry.getValue()) {
//                    Product p2 = productsByTitle.get(p1.getTitle());
//
//                    if (p2 == null) {
//                        continue;
//                    }
//
//                    Map<String, BigDecimal> itemPriceByTitle = p2.getItems().stream()
//                            .collect(Collectors.toMap(Item::getTitle, Item::getPrice));
//
//                    Map<String, Prices> itemResultData = new TreeMap<>();
//                    for (Item i1 : p1.getItems()) {
//                        BigDecimal priceI1 = i1.getPrice();
//                        BigDecimal priceI2 = itemPriceByTitle.get(i1.getTitle());
//
//                        if (!priceI1.equals(priceI2)) {
//                            itemResultData.put(i1.getTitle(), new Prices(priceI1, priceI2));
//                        }
//                    }
//                    productResultData.put(p1.getTitle(), itemResultData);
//                }
//
//            }
//        }
//        return new History(firstA.getCreatedDate(), productResultData);
//    }

//    class Prices {
//        private BigDecimal oldPrice;
//        private BigDecimal newPrice;
//
//        public Prices(BigDecimal oldPrice, BigDecimal newPrice) {
//            this.oldPrice = oldPrice;
//            this.newPrice = newPrice;
//        }
//
//        public BigDecimal getOldPrice() {
//            return oldPrice;
//        }
//
//        public BigDecimal getNewPrice() {
//            return newPrice;
//        }
//    }
//
//    class History {
//        private LocalDateTime date;
//        private Map<String, Map<String, Prices>> history;
//
//        public History(LocalDateTime date, Map<String, Map<String, Prices>> history) {
//            this.date = date;
//            this.history = history;
//        }
//
//        public LocalDateTime getDate() {
//            return date;
//        }
//
//        public Map<String, Map<String, Prices>> getHistory() {
//            return history;
//        }
//    }

}
