package ru.proshik.applepriceparcer.provider;

import ru.proshik.applepriceparcer.provider.model.Assortment;
import ru.proshik.applepriceparcer.provider.model.Item;
import ru.proshik.applepriceparcer.provider.model.Product;

public class ProviderUtils {

    public static String buildAssortmentString(Assortment assortment) {
        StringBuilder out = new StringBuilder("Date: " + assortment.getCreatedDate().toString() + "\n");

        for (Product p : assortment.getProducts()) {
            out.append("### ").append(p.getTitle()).append(" ###").append("\n");
            for (Item i : p.getItems()) {
                out.append(i.getTitle()).append(" - ").append(i.getPrice()).append("\n");
            }
            out.append("\r\n");
        }

        return out.toString();
    }

}
