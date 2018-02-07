package ru.proshik.applepriceparcer.importshop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model2.Fetch;
import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.provider2.aj.AjProvider;
import ru.proshik.applepriceparcer.storage.Database2;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class OneRead {

    private ObjectMapper objectMapper = new ObjectMapper();

    private void write(Database2 db) throws IOException, DatabaseException {
        objectMapper.registerModule(new JSR310Module());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Shop shop = new Shop(AjProvider.TITLE, AjProvider.URL);

        Fetch[] fetch = objectMapper.readValue(new File(shop.getTitle() + ".json"), Fetch[].class);

        for (Fetch f : Arrays.asList(fetch)) {
            db.addFetch(shop, f);
        }
    }

    public static void main(String[] args) throws IOException, DatabaseException {
        Database2 db = new Database2("database.db");

        OneRead oneRead = new OneRead();

        oneRead.write(db);
    }
}
