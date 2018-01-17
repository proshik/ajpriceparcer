package ru.proshik.applepriceparcer.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.jetbrains.annotations.NotNull;
import org.mapdb.*;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Shop;
import ru.proshik.applepriceparcer.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        DB db = open();

        HTreeMap<Shop, String> map = createOrOpenShopBucket(db);
        String assortmentString = map.get(shop);

        List<Assortment> assortments = new ArrayList<>();
        if (assortmentString != null) {
            try {
                Assortment[] assortmentsArray = mapper.readValue(assortmentString, Assortment[].class);
                if (assortmentsArray != null) {
                    assortments = Stream.of(assortmentsArray).collect(Collectors.toList());
                }
            } catch (IOException e) {
                throw new DatabaseException(e);
            } finally {
                db.close();
            }
        }

        assortments.add(assortment);

        try {
            String updatedAssortmentString = mapper.writeValueAsString(assortments);
            map.put(shop, updatedAssortmentString);
        } catch (JsonProcessingException e) {
            throw new DatabaseException(e);
        } finally {
            db.close();
        }
    }

    public List<Assortment> getAssortments(Shop shop) throws DatabaseException {
        List<Assortment> result = new ArrayList<>();

        DB db = open();
        HTreeMap<Shop, String> map = createOrOpenShopBucket(db);
        try {
            String assortmentString = map.get(shop);
            if (assortmentString != null) {
                result = mapper.readValue(assortmentString, new TypeReference<List<Assortment>>() {
                });
            }
        } catch (IOException e) {
            throw new DatabaseException(e);
        } finally {
            db.close();
        }

        return result;
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

    private HTreeMap<Shop, String> createOrOpenShopBucket(DB db) {
        return db.hashMap(SHOP_BUCKET)
                .keySerializer(serializer)
                .valueSerializer(Serializer.STRING)
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

    private class UserSerializer implements Serializer<User>, Serializable {

        @Override
        public void serialize(@NotNull DataOutput2 out, @NotNull User value) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(value);

            out.write(bos.toByteArray());
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

