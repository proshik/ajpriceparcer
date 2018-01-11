package ru.proshik.applepriceparcer.storage;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import ru.proshik.applepriceparcer.provider.model.Assortment;
import ru.proshik.applepriceparcer.provider.model.Shop;

public class Database {

    private final String dbPath;

    public Database(String dbPath) {
        this.dbPath = dbPath;
    }

    public void init() {

    }

    public void save(Shop shop, Assortment assortment) {
        DB db = DBMaker
                .fileDB(dbPath)
                .fileMmapEnable()
                .make();

        db.close();
    }

}