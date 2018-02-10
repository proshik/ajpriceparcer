package ru.proshik.applepricesbot.utils.importshop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import ru.proshik.applepricesbot.exception.DatabaseException;
import ru.proshik.applepricesbot.provider.ProviderFactory;
import ru.proshik.applepricesbot.storage.Database;
import ru.proshik.applepricesbot.storage.model.Fetch;
import ru.proshik.applepricesbot.storage.model.Shop;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class ImportFromJsonFile {

    private ObjectMapper objectMapper = new ObjectMapper();

    private ProviderFactory providerFactory = new ProviderFactory();

    private void read(Database db) throws IOException, DatabaseException {
        objectMapper.registerModule(new JSR310Module());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Set<Shop> shops = providerFactory.getShops();

        for (Shop shop : shops) {
            Fetch[] fetch = objectMapper.readValue(new File(shop.getTitle() + ".json"), Fetch[].class);

            if (fetch.length == 0) {
                System.out.println("Data for shop " + shop.getTitle() + " is empty!");
            }

            for (Fetch f : Arrays.asList(fetch)) {
                db.addFetch(shop, f);
            }

            System.out.println("Success import: " + shop.getTitle());
        }
    }

    public static void main(String[] args) throws IOException, DatabaseException {
        Database db = new Database("database.db");

        ImportFromJsonFile importFromJsonFile = new ImportFromJsonFile();
        importFromJsonFile.read(db);
    }
}
