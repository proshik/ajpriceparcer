package ru.proshik.applepriceparcer.storage;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model2.Fetch;
import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.model2.User;
import ru.proshik.applepriceparcer.storage.serializer2.FetchListSerializer;
import ru.proshik.applepriceparcer.storage.serializer2.ShopSerializer;
import ru.proshik.applepriceparcer.storage.serializer2.UserSerializer;

import java.util.ArrayList;
import java.util.List;

public class Database2 {

    private static final String SHOP_BUCKET = "shop";
    private static final String USER_BUCKET = "user";

    private final String dbPath;

    public Database2(String dbPath) {
        this.dbPath = dbPath;
    }

    public void addFetch(Shop shop, Fetch fetch) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<Shop, List<Fetch>> map = createOrOpenShopBucket(db);
            List<Fetch> fetchList = map.get(shop);
            if (fetchList == null) {
                List<Fetch> a = new ArrayList<>();
                a.add(fetch);
                map.put(shop, a);
            } else {
                fetchList.add(fetch);
                map.put(shop, fetchList);
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public List<Fetch> getFetches(Shop shop) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<Shop, List<Fetch>> map = createOrOpenShopBucket(db);
            return map.get(shop);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public void addUserShop(String chatId, Shop shop) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<String, User> map = createOrOpenUserBucket(db);
            User user = map.get(chatId);

            if (user == null) {
                user = new User();
            }

            user.getSubscriptions().add(shop);

            map.put(chatId, user);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public User getUser(String chatId) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<String, User> map = createOrOpenUserBucket(db);
            return map.get(chatId);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    private DB open() {
        return DBMaker
                .fileDB(dbPath)
                .fileMmapEnable()
                .fileLockWait(3000)
                .make();
    }

    private HTreeMap<Shop, List<Fetch>> createOrOpenShopBucket(DB db) {
        return db.hashMap(SHOP_BUCKET)
                .keySerializer(new ShopSerializer())
                .valueSerializer(new FetchListSerializer())
                .createOrOpen();
    }

    private HTreeMap<String, User> createOrOpenUserBucket(DB db) {
        return db.hashMap(USER_BUCKET)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new UserSerializer())
                .createOrOpen();
    }

}

