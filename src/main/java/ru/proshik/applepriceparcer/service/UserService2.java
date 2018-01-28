package ru.proshik.applepriceparcer.service;

import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.model2.User;
import ru.proshik.applepriceparcer.storage.Database2;

import java.util.Collections;
import java.util.Set;

public class UserService2 {

    private Database2 db;

    public UserService2(Database2 db) {
        this.db = db;
    }

    public Set<Shop> userSubscriptions(String chatId) throws DatabaseException {
        User user = db.getUser(chatId);

        if (user != null) {
            return user.getSubscriptions();
        } else {
            return Collections.emptySet();
        }
    }

    public void addSubscription(String chatId, Shop shop) throws DatabaseException {
        db.addUserShop(chatId, shop);
    }
}
