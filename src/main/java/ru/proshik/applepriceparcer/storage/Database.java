package ru.proshik.applepriceparcer.storage;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import ru.proshik.applepriceparcer.provider.model.Assortment;
import ru.proshik.applepriceparcer.provider.model.Shop;

import java.util.concurrent.ConcurrentMap;

public class Database {

    private final String dbPath;

    public Database(String dbPath) {
        this.dbPath = dbPath;
    }

    public void init() {
        //test
        DB db = DBMaker
                .fileDB(dbPath)
                .fileMmapEnable()
                .make();

        ConcurrentMap<String, Long> map = db
                .hashMap("map", Serializer.STRING, Serializer.LONG)
                .createOrOpen();
        map.put("something", 111L);

        db.close();
    }

    public void save(Shop shop, Assortment assortment) {

    }

}