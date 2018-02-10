package ru.proshik.applepricebot.utils;

import ru.proshik.applepricebot.dto.ChangeProductNotification;
import ru.proshik.applepricebot.dto.DiffProducts;
import ru.proshik.applepricebot.dto.HistoryDiff;
import ru.proshik.applepricebot.storage.model.Product;
import ru.proshik.applepricebot.storage.model.ProductType;
import ru.proshik.applepricebot.storage.model.Shop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PrintUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static String fetchInfo(String shopTitle,
                                   ProductType productType,
                                   LocalDateTime createdDate,
                                   List<Product> products) {
        return "Shop: *" + shopTitle + "*\n" +
                "Product type: *" + productType.getValue() + "*\n" +
                "Date last change prices: *" + DATE_TIME_FORMATTER.format(createdDate) + "*\n\n" +
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

    public static String historyInfo(Shop shop, List<HistoryDiff> historyDiff) {
        StringBuilder out = new StringBuilder();

        if (historyDiff.isEmpty()) {
            out.append("*Changes was not found for shop: ").append(shop.getTitle()).append("*\n\n");
        } else {
            out.append("*History for shop: ").append(shop.getTitle()).append("*\n\n");

            for (HistoryDiff hd : historyDiff) {
                if (hd.getDiff().isEmpty()) {
                    continue;
                }
                out.append("*Dates, old/new: ")
                        .append(DATE_TIME_FORMATTER.format(hd.getOldCreatedDate()))
                        .append("/")
                        .append(DATE_TIME_FORMATTER.format(hd.getNewCreatedDAte())).append("*\n");

                diffProducts(hd.getDiff(), out);
            }
        }

        return out.toString();
    }

    public static String notificationInfo(ChangeProductNotification notification) {
        StringBuilder out = new StringBuilder();
        out.append("*Notification for shop: ").append(notification.getShop().getTitle()).append("*\n\n");

        diffProducts(notification.getDiffProducts(), out);

        return out.toString();
    }

    private static void diffProducts(List<DiffProducts> diffProducts, StringBuilder out) {

        diffProducts.sort((o1, o2) -> {
            if (o1.getNewProductDesc() != null && o2.getNewProductDesc() != null) {
                return o1.getNewProductDesc().getPrice().compareTo(o2.getNewProductDesc().getPrice());
            } else {
                return o1.getOldProductDesc().getPrice().compareTo(o2.getOldProductDesc().getPrice());
            }
        });

        for (DiffProducts diffP : diffProducts) {

            Product oldProductDesc = diffP.getOldProductDesc();
            Product newProductDesc = diffP.getNewProductDesc();

            if (newProductDesc == null) {
                out.append("*").append(oldProductDesc.getTitle()).append("*\n");
                out.append(oldProductDesc.getDescription()).append("\n");
                out.append("*Product of deleted!*\n");
            } else {
                out.append("*").append(newProductDesc.getTitle()).append("*\n");
                out.append(newProductDesc.getDescription()).append("\n");
                if (oldProductDesc != null) {
                    if (!oldProductDesc.getPrice().equals(newProductDesc.getPrice())) {
                        out.append("Prices old/new: ")
                                .append(oldProductDesc.getPrice()).append("/").append("*").append(newProductDesc.getPrice()).append("*\n");
                    }
                    if (oldProductDesc.getAvailable() != null && newProductDesc.getAvailable() != null
//                            && (!oldProductDesc.getAvailable().equals(newProductDesc.getAvailable()))
                            ) {
                        out.append("Available before/now: ")
                                .append(oldProductDesc.getAvailable()).append("/").append("*").append(newProductDesc.getAvailable()).append("*\n");
                    }
                } else {
                    out.append("*Product were added!*\n");
                    out.append("Price: *").append(newProductDesc.getPrice()).append("*\n");
                    if (newProductDesc.getAvailable() != null) {
                        out.append("Available: *").append(newProductDesc.getAvailable()).append("*\n");
                    }
                }
            }
            out.append("\n");
        }
    }

    private static String printProducts(List<Product> products) {
        StringBuilder out = new StringBuilder();

        products.sort(Comparator.comparing(Product::getPrice));
        for (Product p : products) {
            out.append("*").append(p.getTitle()).append("*\n");
            out.append(p.getDescription()).append("\n");
            out.append("Price: ").append("*").append(p.getPrice()).append("*\n");
            if (p.getAvailable() != null) {
                out.append("Available: ").append("*").append(p.getAvailable().toString()).append("*\n\n");
            } else {
                out.append("\n");
            }
        }

        return out.toString();
    }

}
