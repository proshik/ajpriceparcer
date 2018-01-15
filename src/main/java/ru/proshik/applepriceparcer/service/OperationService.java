package ru.proshik.applepriceparcer.service;

import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.exception.ProviderParseException;
import ru.proshik.applepriceparcer.model.*;
import ru.proshik.applepriceparcer.provider.Provider;
import ru.proshik.applepriceparcer.provider.ProviderFactory;
import ru.proshik.applepriceparcer.storage.Database;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OperationService {

    private static final Logger LOG = Logger.getLogger(OperationService.class);

    private static final int HISTORY_LIMIT = 5;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private Database db;
    private ProviderFactory providerFactory;

    public OperationService(Database db, ProviderFactory providerFactory) {
        this.db = db;
        this.providerFactory = providerFactory;
    }

    public Shop findShopByTitle(String title) {
        Provider provider = providerFactory.list().stream()
                .filter(p -> p.getShop().getTitle().toUpperCase().equals(title.toUpperCase()))
                .findFirst()
                .orElse(null);

        if (provider != null) {
            return provider.getShop();
        } else {
            return null;
        }
    }

    public List<Shop> selectAvailableShops() {
        return providerFactory.list().stream()
                .map(Provider::getShop)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Provider> selectAllProviders() {
        return providerFactory.list();
    }

    public List<ProductType> selectUniqueProductTypes(Shop shop) {
        try {
            List<Assortment> assortments = db.getAssortments(shop);

            return assortments.stream()
                    .flatMap(assortment -> assortment.getProducts().stream())
                    .map(Product::getProductType)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (DatabaseException e) {
            LOG.error("Error on select unique product types", e);
        }

        return Collections.emptyList();
    }

    public String read(Shop shop, ProductType productType) {
        String result;

        Provider provider = providerFactory.findByShop(shop);
        try {
            Assortment assortment = provider.screening();

//                try {
//                    // TODO: 12.01.2018 will be need read from cache(in this class), method tryUpdateAssortment will be moved to scheduler
//                    shopService.tryUpdateAssortment(shop, assortment);
//                } catch (DatabaseException e) {
//                    LOG.error("error", e);
//                }

            result = buildAssortment(assortment);
        } catch (ProviderParseException e) {
            LOG.error("Erorr on read data from provider with title=" + shop.getTitle(), e);
            result = "Error on executing command";
        }
//        } else {
//            result = "Title unrecognized";
//        }
        return result;
    }

    public String history(Shop shop, ProductType productType) {
        String result;

        Provider provider = providerFactory.findByShop(shop);
        if (provider != null) {
            try {
                List<Assortment> assortments = db.getAssortments(shop);

                List<Assortment> sortAssortments = assortments.stream()
                        .sorted((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()))
                        .limit(HISTORY_LIMIT)
                        .collect(Collectors.toList());

                result = buildHistory(sortAssortments);
            } catch (DatabaseException e) {
                LOG.error("Error on execute history command", e);
                result = "Error on executing command";
            }
        } else {
            result = "Title unrecognized";
        }

        return result;
    }

    public void tryUpdateAssortment(Shop shop, Assortment assortment) throws DatabaseException {
        List<Assortment> existsAssortments = db.getAssortments(shop);

        if (existsAssortments != null && !existsAssortments.isEmpty()) {
            boolean wasChanges = wasChangeInAssortments(assortment, existsAssortments);
            if (wasChanges) {
                db.addAssortment(shop, assortment);
                LOG.info("Success updated assortment for shop with title=" + shop.getTitle());
            }
        } else {
            db.addAssortment(shop, assortment);
            LOG.info("Success added at first time assortment for shop with title=" + shop.getTitle());
        }
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


    private static String buildAssortment(Assortment assortment) {
        StringBuilder out = new StringBuilder("Date: *" + DATE_TIME_FORMATTER.format(assortment.getCreatedDate()) + "*\n");

        for (Product p : assortment.getProducts()) {
            out.append(p.getTitle()).append("\n");
            for (Item i : p.getItems()) {
                out.append(i.getTitle()).append(" - ").append(i.getPrice()).append("\n");
            }
            out.append("\n");
        }

        return out.toString();
    }

    private String buildHistory(List<Assortment> assortments) {
        StringBuilder history = new StringBuilder("History:\n");

        for (Assortment a : assortments) {
//            history.append("-----------------").append("\n");
            history.append(buildAssortment(a));
//            history.append("*****************").append("\n\n");
        }

        return history.toString();
    }
}
