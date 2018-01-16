package ru.proshik.applepriceparcer.bot;

import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Item;
import ru.proshik.applepriceparcer.model.Product;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PrintUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");


    static String printAssortment(Assortment assortment) {

        StringBuilder out = new StringBuilder("Date last change prices: *" + DATE_TIME_FORMATTER.format(assortment.getCreatedDate()) + "*\n");

        for (Product p : assortment.getProducts()) {
            out.append(p.getTitle()).append("\n");
            for (Item i : p.getItems()) {
                out.append(i.getTitle()).append(" - ").append(i.getPrice()).append("\n");
            }
            out.append("\n");
        }

        return out.toString();
    }

    static String buildHistory(List<Assortment> assortments) {
        StringBuilder history = new StringBuilder("History:\n");

        for (Assortment a : assortments) {
//            historyAssortments.append("-----------------").append("\n");
            history.append(printAssortment(a));
//            historyAssortments.append("*****************").append("\n\n");
        }

        return history.toString();
    }

}
