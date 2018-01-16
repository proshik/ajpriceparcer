package ru.proshik.applepriceparcer.service;

import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model.Shop;
import ru.proshik.applepriceparcer.storage.Database;

import java.util.List;

public class UserService {

    private Database db;

    public UserService(Database db) {
        this.db = db;
    }

    public List<Shop> userSubscriptions(String chatId) throws DatabaseException {
        return db.getUserShops(chatId);
    }

    public void addSubscription(String chatId, Shop shop) throws DatabaseException {
        db.addUserShop(chatId, shop);
    }
}
