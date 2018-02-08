package ru.proshik.applepriceparcer.bot;

import ru.proshik.applepriceparcer.model2.*;

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

    public static String historyInfo(Shop shop, List<HistoryDiff> historyDiff) {
        StringBuilder out = new StringBuilder();
        out.append("*History for shop: ").append(shop.getTitle()).append("*\n\n");

        for (HistoryDiff hd : historyDiff) {
            out.append("*Dates, old/new: ")
                    .append(DATE_TIME_FORMATTER.format(hd.getOldCreatedDate()))
                    .append("/")
                    .append(DATE_TIME_FORMATTER.format(hd.getNewCreatedDAte())).append("*\n");

            diffProducts(hd.getDiff(), out);
        }

        return out.toString();
    }

    static String notificationInfo(ChangeProductNotification notification) {
        StringBuilder out = new StringBuilder();
        out.append("*Notification for shop: ").append(notification.getShop().getTitle()).append("*\n\n");

        diffProducts(notification.getDiffProducts(), out);

        return out.toString();
    }

    private static void diffProducts(List<DiffProducts> diffProducts, StringBuilder out) {

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
                            && (!oldProductDesc.getAvailable().equals(newProductDesc.getAvailable()))) {
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
