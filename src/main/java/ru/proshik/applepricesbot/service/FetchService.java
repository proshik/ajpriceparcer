package ru.proshik.applepricesbot.service;

import org.apache.log4j.Logger;
import ru.proshik.applepricesbot.exception.DatabaseException;
import ru.proshik.applepricesbot.storage.model.Fetch;
import ru.proshik.applepricesbot.storage.model.Shop;
import ru.proshik.applepricesbot.storage.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchService {

    private static final Logger LOG = Logger.getLogger(FetchService.class);

    private Database db;

    private Map<Shop, List<Fetch>> fetchCache = new HashMap<>();

    public FetchService(Database db) {
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
