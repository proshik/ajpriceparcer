package ru.proshik.applepricebot.utils.importshop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import ru.proshik.applepricebot.exception.DatabaseException;
import ru.proshik.applepricebot.storage.model.Fetch;
import ru.proshik.applepricebot.storage.model.Shop;
import ru.proshik.applepricebot.provider.aj.AjProvider;
import ru.proshik.applepricebot.storage.Database;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class OneRead {

    private ObjectMapper objectMapper = new ObjectMapper();

    private void write(Database db) throws IOException, DatabaseException {
        objectMapper.registerModule(new JSR310Module());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Shop shop = new Shop(AjProvider.TITLE, AjProvider.URL);

        Fetch[] fetch = objectMapper.readValue(new File(shop.getTitle() + ".json"), Fetch[].class);

        for (Fetch f : Arrays.asList(fetch)) {
            db.addFetch(shop, f);
        }
    }

    public static void main(String[] args) throws IOException, DatabaseException {
        Database db = new Database("database.db");

        OneRead oneRead = new OneRead();

        oneRead.write(db);
    }
}
