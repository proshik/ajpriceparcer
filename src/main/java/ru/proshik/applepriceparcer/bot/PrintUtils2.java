package ru.proshik.applepriceparcer.bot;


import ru.proshik.applepriceparcer.model2.Product;
import ru.proshik.applepriceparcer.model2.ProductType;
import ru.proshik.applepriceparcer.model2.Shop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PrintUtils2 {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static String fetchInfo(String shopTitle,
                                   ProductType productType,
                                   LocalDateTime createdDate,
                                   List<Product> products) {
        return "Shop: *" + shopTitle + "*\n" +
                "Product type: *" + productType.getValue() + "*\n\n" +
                "*Date last change prices: *" +
                DATE_TIME_FORMATTER.format(createdDate) +
                "\n" +
                printProducts(products);
    }

    public static String subscriptionInfo(Set<Shop> userSubscriptions, Set<Shop> availableShops) {
        StringBuilder builder = new StringBuilder();

        if (userSubscriptions.isEmpty()) {
            builder.append("*You not have any one subscriptions.*\n\n");
        } else {
            String userShops = userSubscriptions.stream()
                    .map(shop -> "*" + shop.getTitle() + "* - " + shop.getUrl().replace("http://", ""))
                    .collect(Collectors.joining("\n"));
            builder.append("Your subscriptions:\n").append(userShops).append("\n\n");
        }

        if (!availableShops.isEmpty()) {
            String shopsForSubscribe = availableShops.stream()
                    .map(shop -> "*" + shop.getTitle() + "* - " + shop.getUrl().replace("http://", ""))
                    .collect(Collectors.joining("\n"));

            builder.append("You could subscribe at:\n").append(shopsForSubscribe);
        }

        return builder.toString();
    }


//    static String buildHistory(String shopTitle, List<Assortment> assortments, ProductType productType) {
//        StringBuilder out = new StringBuilder();
//        out.append("*History* operation\n")
//                .append("Shop: *").append(shopTitle).append("*\n")
//                .append("Product type: *").append(productType.getValue()).append("*\n\n");
//
//        if (assortments.isEmpty()) {
//            out.append("There were no changes in the prices");
//        } else {
//            for (Assortment a : assortments) {
//                out.append("*Date last change prices: *")
//                        .append(DATE_TIME_FORMATTER.format(a.getCreatedDate()))
//                        .append("\n");
//                out.append(printProducts(a.byProductType(productType)));
//            }
//        }
//        return out.toString();
//    }

    private static String printProducts(List<Product> products) {
        products.sort(Comparator.comparing(Product::getPrice));

        StringBuilder out = new StringBuilder();

        for (Product p : products) {
            out.append("*").append(p.getTitle()).append("* - ").append(p.getPrice()).append(" rub.\n");
            out.append(p.getDescription()).append("\n");
            if (p.getAvailable() != null) {
                out.append("Available: ").append("*").append(p.getAvailable().toString()).append("*\n\n");
            } else {
                out.append("\n");
            }
        }

        return out.toString();
    }

}
