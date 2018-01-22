package ru.proshik.applepriceparcer.storage;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Shop;
import ru.proshik.applepriceparcer.model.User;
import ru.proshik.applepriceparcer.storage.serializer.AssortmentListSerializer;
import ru.proshik.applepriceparcer.storage.serializer.ShopSerializer;
import ru.proshik.applepriceparcer.storage.serializer.UserSerializer;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String SHOP_BUCKET = "shop";
    private static final String USER_BUCKET = "user";

    private final String dbPath;

    public Database(String dbPath) {
        this.dbPath = dbPath;
    }

    public void addAssortment(Shop shop, Assortment assortment) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<Shop, List<Assortment>> map = createOrOpenShopBucket(db);
            List<Assortment> assortments = map.get(shop);
            if (assortments == null) {
                List<Assortment> a = new ArrayList<>();
                a.add(assortment);
                map.put(shop, a);
            } else {
                assortments.add(assortment);
                map.put(shop, assortments);
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public List<Assortment> getAssortments(Shop shop) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<Shop, List<Assortment>> map = createOrOpenShopBucket(db);
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

    private HTreeMap<Shop, List<Assortment>> createOrOpenShopBucket(DB db) {
        return db.hashMap(SHOP_BUCKET)
                .keySerializer(new ShopSerializer())
                .valueSerializer(new AssortmentListSerializer())
                .createOrOpen();
    }

    private HTreeMap<String, User> createOrOpenUserBucket(DB db) {
        return db.hashMap(USER_BUCKET)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new UserSerializer())
                .createOrOpen();
    }

}

