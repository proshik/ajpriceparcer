package ru.proshik.applepriceparcer.bot;


import ru.proshik.applepriceparcer.model2.Fetch;
import ru.proshik.applepriceparcer.model2.Product;
import ru.proshik.applepriceparcer.model2.ProductType;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PrintUtils2 {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    static String printFetch(String shopTitle, Fetch fetch, ProductType productType) {
        return "*Price* operation\n" +
                "Shop: *" + shopTitle + "*\n" +
                "Product type: *" + productType.getValue() + "*\n\n" +
                "*Date last change prices: *" +
                DATE_TIME_FORMATTER.format(fetch.getCreatedDate()) +
                "\n" +
                printProducts(fetch.getProducts(), productType);
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

    private static String printProducts(List<Product> products, ProductType productType) {
        List<Product> filteredProducts = products.stream()
                .filter(product -> product.getProductType() == productType)
                .collect(Collectors.toList());

        StringBuilder out = new StringBuilder();

//        for (Assortment a : products) {
//            out.append(a.getTitle()).append("\n");
        for (Product p : filteredProducts) {
            out.append(p.getTitle()).append(" - ").append(p.getPrice()).append("\n");
        }
//            out.append("\n");
//        }

        return out.toString();
    }

}
