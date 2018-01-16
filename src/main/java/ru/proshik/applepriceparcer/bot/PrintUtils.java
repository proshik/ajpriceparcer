package ru.proshik.applepriceparcer.bot;

import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Item;
import ru.proshik.applepriceparcer.model.Product;
import ru.proshik.applepriceparcer.model.ProductType;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrintUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    static String printAssortment(Assortment assortment, ProductType productType) {
        StringBuilder out = new StringBuilder();
        out.append("*Date last change prices: *")
                .append(DATE_TIME_FORMATTER.format(assortment.getCreatedDate()))
                .append("\n");
        out.append(printProducts(assortment.byProductType(productType)));

        return out.toString();
    }

    static String buildHistory(List<Assortment> assortments, ProductType productType) {
        StringBuilder out = new StringBuilder();

        for (Assortment a : assortments) {
            out.append("*Date last change prices: *")
                    .append(DATE_TIME_FORMATTER.format(a.getCreatedDate()))
                    .append("\n");
            out.append(printProducts(a.byProductType(productType)));
        }

        return out.toString();
    }

    private static String printProducts(List<Product> products) {
        StringBuilder out = new StringBuilder();

        for (Product p : products) {
            out.append(p.getTitle()).append("\n");
            for (Item i : p.getItems()) {
                out.append(i.getTitle()).append(" - ").append(i.getPrice()).append("\n");
            }
            out.append("\n");
        }

        return out.toString();
    }

}
