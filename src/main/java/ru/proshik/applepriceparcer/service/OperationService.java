package ru.proshik.applepriceparcer.service;

import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.exception.ServiceLayerException;
import ru.proshik.applepriceparcer.model.*;
import ru.proshik.applepriceparcer.provider.Provider;
import ru.proshik.applepriceparcer.provider.ProviderFactory;
import ru.proshik.applepriceparcer.storage.Database;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class OperationService {

    private static final Logger LOG = Logger.getLogger(OperationService.class);

    private static final int HISTORY_LIMIT = 5;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    //    private Database db;
    private ProviderFactory providerFactory;
    private AssortmentService assortmentService;

    public OperationService(ProviderFactory providerFactory, AssortmentService assortmentService) {
        this.providerFactory = providerFactory;
        this.assortmentService = assortmentService;
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
            List<Assortment> assortments = assortmentService.getAssortments(shop);

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

    public Assortment priceAssortment(Shop shop, ProductType productType) throws ServiceLayerException {

        List<Assortment> assortments = assortmentService.getAssortments(shop);
        if (assortments.isEmpty()) {
            return null;
        }

        Assortment lastAssortment = findLastAssortment(assortments);
        List<Product> productsByType = lastAssortment.getProducts().stream()
                .filter(product -> product.getProductType().equals(productType))
                .collect(Collectors.toList());

        return new Assortment(lastAssortment.getCreatedDate(), productsByType);
    }

    public List<Assortment> historyAssortments(Shop shop, ProductType productType) {

        Provider provider = providerFactory.findByShop(shop);
        if (provider != null) {
            try {
                List<Assortment> assortments = assortmentService.getAssortments(shop);

                List<Assortment> sortAssortments = assortments.stream()
                        .sorted((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()))
                        .limit(HISTORY_LIMIT)
                        .collect(Collectors.toList());

                return sortAssortments;
            } catch (DatabaseException e) {
                LOG.error("Error on execute historyAssortments command", e);
            }
        } else {
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    public void tryUpdateAssortment(Shop shop, Assortment assortment) throws ServiceLayerException {
        List<Assortment> existsAssortments = assortmentService.getAssortments(shop);

        if (existsAssortments != null && !existsAssortments.isEmpty()) {
            boolean wasChanges = wasChangeInAssortments(assortment, existsAssortments);
            if (wasChanges) {
                assortmentService.addAssortment(shop, assortment);
                LOG.info("Success updated assortment for shop with title=" + shop.getTitle());
            }
        } else {
            assortmentService.addAssortment(shop, assortment);
            LOG.info("Success2 added at first time assortment for shop with title=" + shop.getTitle());
        }
    }

    private static boolean wasChangeInAssortments(Assortment newAssortment, List<Assortment> existsAssortments) {
        Assortment lastAssortment = findLastAssortment(existsAssortments);

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

    private static Assortment findLastAssortment(List<Assortment> assortments) {
        return assortments.stream()
                .max(Comparator.comparing(Assortment::getCreatedDate))
                .orElseThrow(() -> new IllegalArgumentException("Must be at least one element"));
    }

}
