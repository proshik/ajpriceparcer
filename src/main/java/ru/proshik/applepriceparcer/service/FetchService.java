package ru.proshik.applepriceparcer.service;

import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model2.Fetch;
import ru.proshik.applepriceparcer.model2.ProductType;
import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.storage.Database2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchService {

    private static final Logger LOG = Logger.getLogger(FetchService.class);

    private Database2 db;

    private Map<Shop, List<Fetch>> fetchCache = new HashMap<>();

    public FetchService(Database2 db) {
        this.db = db;
    }

    public List<Fetch> getFetch(Shop shop) throws DatabaseException {
        List<Fetch> fetch = fetchCache.get(shop);
        if (fetch != null && !fetch.isEmpty()) {
            return fetch;
        } else {
            LOG.info("Value for shop=" + shop.getTitle() + " not found in cache. He will be updated.");

            fetch = db.getFetches(shop);
            if (fetch != null) {
                fetchCache.computeIfAbsent(shop, k -> new ArrayList<>())
                        .addAll(fetch);
            }
            return fetch;
        }
    }

    public void addFetch(Shop shop, Fetch fetch) throws DatabaseException {
        db.addFetch(shop, fetch);
        //invalidating cache
        fetchCache.clear();
    }

}
