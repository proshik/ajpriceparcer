package ru.proshik.applepriceparcer.service;

import ru.proshik.applepriceparcer.model2.ProductType;
import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.provider2.ProviderFactory;

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

//    private CacheService shopCache = new CacheService();

    //        if (option != null && option.getTitle().equals("-h")) {
//            System.out.println(printInfo());
//            return;
//        }
//
//        List<Assortment> newAjAssortments = ajScreener.printAjPrices();
//
//        List<Aj> ajAssortments = fileStorage.priceAssortment();
//
//        if (!ajAssortments.isEmpty()) {
//            // get last assortments for compare
//            Aj lastAssortment = CommandUtils.getLastAssortment(ajAssortments);
//            // searching changes in assortments
//            Map<String, List<ImmutablePair<Item, BigDecimal>>> assortmentChanges =
//                    buildAssortmentChanges(newAjAssortments, lastAssortment.getAssortments());
//            // if changes from last date regarding this moment was found then save new assortment and print diff
//            if (!assortmentChanges.isEmpty()) {
//                fileStorage.save(new Aj(LocalDateTime.now(), newAjAssortments));
//
//                System.out.println("Change WAS FOUND!");
//                System.out.println(buildChangesString(lastAssortment.getCreatedDate(), assortmentChanges));
//            } else {
//                System.out.println("Change NOT was found!");
//            }
//        } else {
//            fileStorage.save(new Aj(LocalDateTime.now(), newAjAssortments));
//            System.out.println("Values was saved in first time");
//        }


    //    static Map<String, List<ImmutablePair<Item, BigDecimal>>> buildAssortmentChanges(List<Assortment> newAjAssortments,
//                                                                                     List<Assortment> lastAssortments) {
//        Map<ImmutablePair<String, String>, Item> savedAssortments = lastAssortments.stream()
//                .flatMap(assortment -> assortment.getItems().stream()
//                        .map(item -> new ImmutablePair<>(assortment, item)))
//                .collect(Collectors.toMap(o -> new ImmutablePair<>(o.left.getTitle(), o.right.getTitle()), ImmutablePair::getRight));
//
//        Map<String, List<ImmutablePair<Item, BigDecimal>>> foundChanges = new HashMap<>();
//
//        for (Assortment a : newAjAssortments) {
//            for (Item i : a.getItems()) {
//                Item item = savedAssortments.get(new ImmutablePair<>(a.getTitle(), i.getTitle()));
//                if (item == null) {
//                    foundChanges.computeIfAbsent(a.getTitle(), k -> new ArrayList<>())
//                            .add(new ImmutablePair<>(i, i.getPrice()));
//                    continue;
//                }
//
//                if (!item.getPrice().equals(i.getPrice())) {
//                    foundChanges.computeIfAbsent(a.getTitle(), k -> new ArrayList<>())
//                            .add(new ImmutablePair<>(i, savedAssortments.get(new ImmutablePair<>(a.getTitle(), i.getTitle())).getPrice()));
//                }
//            }
//        }
//
//        return foundChanges;
//    }
//
//    static String buildChangesString(LocalDateTime lastAssortmentCreatedDate,
//                                     Map<String, List<ImmutablePair<Item, BigDecimal>>> assortmentChanges) {
//
//        StringBuilder change = new StringBuilder();
//        change.append("Date: ").append(DATE_TIME_FORMATTER.format(lastAssortmentCreatedDate)).append("\n");
//        change.append("------------------").append("\n");
//
//        for (Map.Entry<String, List<ImmutablePair<Item, BigDecimal>>> entry : assortmentChanges.entrySet()) {
//            change.append("\n").append("*** ").append(entry.getKey()).append(" ***").append("\n");
//            for (ImmutablePair<Item, BigDecimal> i : entry.getValue()) {
//                change.append(i.getLeft().getTitle()).append(": ")
//                        .append(" old - ").append(i.getRight()).append("; new - ").append(i.getLeft().getPrice()).append("\n");
//            }
//        }
//        change.append("\n").append("*****************").append("\n");
//
//        return change.toString();
//    }
//
//    static String buildChangesString(LocalDateTime lastAssortmentCreatedDate, LocalDateTime newAssortmentCreatedDate,
//                                     Map<String, List<ImmutablePair<Item, BigDecimal>>> assortmentChanges) {
//        StringBuilder change = new StringBuilder();
//        change.append("Last price from: ").append(DATE_TIME_FORMATTER.format(lastAssortmentCreatedDate)).append("\n");
//        change.append("Old price from: ").append(DATE_TIME_FORMATTER.format(newAssortmentCreatedDate)).append("\n");
//        change.append("------------------").append("\n");
//
//        for (Map.Entry<String, List<ImmutablePair<Item, BigDecimal>>> entry : assortmentChanges.entrySet()) {
//            change.append("\n").append("*** ").append(entry.getKey()).append(" ***").append("\n");
//            for (ImmutablePair<Item, BigDecimal> i : entry.getValue()) {
//                change.append(i.getLeft().getTitle()).append(": ")
//                        .append(" old - ").append(i.getRight()).append("; new - ").append(i.getLeft().getPrice()).append("\n");
//            }
//        }
//        change.append("\n").append("*****************").append("\n");
//
//        return change.toString();
//    }


}
