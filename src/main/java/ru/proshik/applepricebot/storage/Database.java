package ru.proshik.applepricebot.storage;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.proshik.applepricebot.exception.DatabaseException;
import ru.proshik.applepricebot.storage.model.Fetch;
import ru.proshik.applepricebot.storage.model.Shop;
import ru.proshik.applepricebot.storage.model.UserSubscriptions;
import ru.proshik.applepricebot.storage.serializer.FetchListSerializer;
import ru.proshik.applepricebot.storage.serializer.ShopSerializer;
import ru.proshik.applepricebot.storage.serializer.UserSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Database {

    private static final String SHOP_BUCKET = "shop";
    private static final String USER_SUBSCRIPTIONS_BUCKET = "userSubscriptions";

    @Value("${db.path}")
    private String dbPath;

//    public Database(String dbPath) {
//        this.dbPath = dbPath;
//    }

    public List<Fetch> getFetches(Shop shop) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<Shop, List<Fetch>> map = createOrOpenShopBucket(db);
            return map.get(shop);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
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

    public Map<String, UserSubscriptions> allSubscribers() throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<String, UserSubscriptions> map = createOrOpenUserBucket(db);

            Map<String, UserSubscriptions> subscribers = new HashMap<>();
            for (HTreeMap.Entry<String, UserSubscriptions> entry : map.getEntries()) {
                subscribers.put(entry.getKey(), entry.getValue());
            }

            return subscribers;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public UserSubscriptions getUserSubscriptions(String userId) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<String, UserSubscriptions> map = createOrOpenUserBucket(db);
            UserSubscriptions userSubscriptions = map.get(userId);

            if (userSubscriptions == null) {
                return new UserSubscriptions();
            }

            return userSubscriptions;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public void addSubscription(String userId, Shop shop) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<String, UserSubscriptions> map = createOrOpenUserBucket(db);
            UserSubscriptions userSubscriptions = map.get(userId);

            if (userSubscriptions == null) {
                userSubscriptions = new UserSubscriptions();
            }

            userSubscriptions.getShops().add(shop);

            map.put(userId, userSubscriptions);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public boolean removeSubscription(String userId, Shop shop) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<String, UserSubscriptions> map = createOrOpenUserBucket(db);
            UserSubscriptions userSubscriptions = map.get(userId);

            if (userSubscriptions == null) {
                return false;
            }
            //remove subscription on shop from list of user subscripptions
            boolean result = userSubscriptions.getShops().remove(shop);
            //save updated user subscriptions
            map.put(userId, userSubscriptions);

            return result;
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

    private HTreeMap<String, UserSubscriptions> createOrOpenUserBucket(DB db) {
        return db.hashMap(USER_SUBSCRIPTIONS_BUCKET)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new UserSerializer())
                .createOrOpen();
    }

}