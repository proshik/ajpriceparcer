package ru.proshik.applepriceparcer.service;

import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Shop;
import ru.proshik.applepriceparcer.storage.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssortmentService {

    private static final Logger LOG = Logger.getLogger(AssortmentService.class);

    private Database db;

    private Map<Shop, List<Assortment>> cacheAssortment = new HashMap<>();

    public AssortmentService(Database db) {
        this.db = db;
    }

    public List<Assortment> getAssortments(Shop shop) throws DatabaseException {
        List<Assortment> assortments = cacheAssortment.get(shop);
        if (assortments != null && !assortments.isEmpty()) {
            return assortments;
        } else {
            LOG.info("Value for shop=" + shop.getTitle() + " not found in cache. He will be updated.");

            assortments = db.getAssortments(shop);
            if (assortments != null){
                cacheAssortment.computeIfAbsent(shop, k -> new ArrayList<>())
                        .addAll(assortments);
            }
            return assortments;
        }
    }

    public void addAssortment(Shop shop, Assortment assortment) throws DatabaseException {
        db.addAssortment(shop, assortment);
    }

}
