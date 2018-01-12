package ru.proshik.applepriceparcer.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.mapdb.*;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Shop;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Database {

    private static final Logger LOG = Logger.getLogger(Database.class);

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
                Assortment[] assortmentsArray = mapper.readValue(assortmentString, Assortment[].class);
               result = Arrays.asList(assortmentsArray);
            }
        } catch (IOException e) {
            throw new DatabaseException(e);
        } finally {
            db.close();
        }

        return result;
    }

    private DB open() {
        return DBMaker
                .fileDB(dbPath)
                .fileMmapEnable()
                .make();
    }

    private HTreeMap<Shop, String> createOrOpenShopBucket(DB db) {
        return db.hashMap(SHOP_BUCKET)
                .keySerializer(serializer)
                .valueSerializer(Serializer.STRING)
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
}

