package ru.proshik.applepriceparcer.command.utils;

import ru.proshik.applepriceparcer.model.goods.AjAssortment;
import ru.proshik.applepriceparcer.model.goods.Assortment;
import ru.proshik.applepriceparcer.model.goods.Item;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandUtils {

    public static boolean wasChangeInPrices(List<Assortment> newAssortment, List<AjAssortment> savedAjAssortment) {
        AjAssortment foundSavedAssortment = getLastAssortment(savedAjAssortment);

        Map<String, Assortment> groupByNewAssortmentTitle = newAssortment.stream()
                .collect(Collectors.toMap(Assortment::getTitle, o -> o));

        for (Assortment a : foundSavedAssortment.getAssortments()) {

            Map<String, BigDecimal> itemPriceByTitle = a.getItems().stream()
                    .collect(Collectors.toMap(Item::getTitle, Item::getPrice));

            List<Item> newItemsInAssortment = groupByNewAssortmentTitle.get(a.getTitle()).getItems();
            for (Item i : newItemsInAssortment) {
                BigDecimal bigDecimal = itemPriceByTitle.get(i.getTitle());
                if (bigDecimal == null) {
                    return true;
                }
                //if price from new assortment not equals
                if (!itemPriceByTitle.get(i.getTitle()).equals(i.getPrice())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static AjAssortment getLastAssortment(List<AjAssortment> ajAssortments) {
        return ajAssortments.stream()
                .max(Comparator.comparing(AjAssortment::getCreatedDate))
                .orElseThrow(() -> new IllegalArgumentException("Must be at least one element"));
    }

    public static String buildAssortmentOut(List<Assortment> newAjAssortments) {
        StringBuilder p = new StringBuilder();

        for (Assortment a : newAjAssortments) {
            p.append(a.getTitle()).append("\n");
            for (Item i : a.getItems()) {
                p.append(i.getTitle()).append(" - ").append(i.getPrice()).append("\n");
            }
            p.append("\r\n");
        }

        return p.toString();
    }

}
