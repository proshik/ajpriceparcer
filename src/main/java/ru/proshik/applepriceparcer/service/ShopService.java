package ru.proshik.applepriceparcer.service;

import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Item;
import ru.proshik.applepriceparcer.model.Product;
import ru.proshik.applepriceparcer.model.Shop;
import ru.proshik.applepriceparcer.provider.ScreenerProviderFactory;
import ru.proshik.applepriceparcer.storage.Database;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopService {

    private Database db;
    private ScreenerProviderFactory screenerProviderFactory;

//    private ShopCache shopCache = new ShopCache();

    public ShopService(Database db, ScreenerProviderFactory screenerProviderFactory) {
        this.db = db;
        this.screenerProviderFactory = screenerProviderFactory;
    }

    public void tryUpdateAssortment(Shop shop, Assortment assortment) throws DatabaseException {
        List<Assortment> existsAssortments = db.getAssortments(shop);

        if (existsAssortments != null && !existsAssortments.isEmpty()) {
            boolean wasChanges = wasChangeInAssortments(assortment, existsAssortments);
            if (wasChanges) {
                db.addAssortment(shop, assortment);
            }
        } else {
            db.addAssortment(shop, assortment);
        }
    }

    public List<Assortment> history(Shop shop) throws DatabaseException {
        return db.getAssortments(shop);
    }

    private static boolean wasChangeInAssortments(Assortment newAssortment, List<Assortment> existsAssortments) {
        Assortment lastAssortment = getLastAssortment(existsAssortments);

        Map<String, Product> productsByTitle = newAssortment.getProducts().stream()
                .collect(Collectors.toMap(Product::getTitle, o -> o));

        for (Product p : lastAssortment.getProducts()) {
            Map<String, BigDecimal> itemPriceByTitle = p.getItems().stream()
                    .collect(Collectors.toMap(Item::getTitle, Item::getPrice));

            List<Item> newItemsInAssortment = productsByTitle.get(p.getTitle()).getItems();
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

    private static Assortment getLastAssortment(List<Assortment> assortments) {
        return assortments.stream()
                .max(Comparator.comparing(Assortment::getCreatedDate))
                .orElseThrow(() -> new IllegalArgumentException("Must be at least one element"));
    }

}
