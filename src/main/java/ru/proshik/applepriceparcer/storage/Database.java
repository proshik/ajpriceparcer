package ru.proshik.applepriceparcer.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.jetbrains.annotations.NotNull;
import org.mapdb.*;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Shop;
import ru.proshik.applepriceparcer.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String SHOP_BUCKET = "shop";
    private static final String USER_BUCKET = "user";

    private final String dbPath;

    private ObjectMapper mapper = new ObjectMapper();
    private Serializer<Shop> serializer = new ShopSerializer();

    public Database(String dbPath) {
        this.dbPath = dbPath;

        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
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
                .keySerializer(serializer)
                .valueSerializer(new AssormentListSerializer())
                .createOrOpen();
    }

    private HTreeMap<String, User> createOrOpenUserBucket(DB db) {
        return db.hashMap(USER_BUCKET)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new UserSerializer())
                .createOrOpen();
    }

    private class ShopSerializer implements Serializer<Shop>, Serializable {

        @Override
        public void serialize(@NotNull DataOutput2 out, @NotNull Shop value) throws IOException {
            out.writeUTF(value.getTitle());
            out.writeUTF(value.getUrl());
        }

        @Override
        public Shop deserialize(@NotNull DataInput2 input, int available) throws IOException {
            return new Shop(input.readUTF(), input.readUTF());
        }
    }

    private class AssormentListSerializer implements Serializer<List<Assortment>>, Serializable {

        @Override
        public void serialize(@NotNull DataOutput2 out, @NotNull List<Assortment> value) throws IOException {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(value);
        }

        @Override
        public List<Assortment> deserialize(@NotNull DataInput2 input, int available) throws IOException {
            try {
                ObjectInputStream in2 = new ObjectInputStream(new DataInput2.DataInputToStream(input));
                return (List) in2.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
        }
    }

    private class UserSerializer implements Serializer<User>, Serializable {

        @Override
        public void serialize(@NotNull DataOutput2 out, @NotNull User value) throws IOException {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(value);
        }

        @Override
        public User deserialize(@NotNull DataInput2 input, int available) throws IOException {
            try {
                ObjectInputStream in2 = new ObjectInputStream(new DataInput2.DataInputToStream(input));
                return (User) in2.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
        }
    }
}

