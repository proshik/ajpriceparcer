package ru.proshik.applepricebot.utils.exportshop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import ru.proshik.applepricebot.exception.DatabaseException;
import ru.proshik.applepricebot.provider.ProviderFactory;
import ru.proshik.applepricebot.storage.Database;
import ru.proshik.applepricebot.storage.model.Fetch;
import ru.proshik.applepricebot.storage.model.Shop;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class ExportToJsonFile {

    private ObjectMapper objectMapper = new ObjectMapper();

    private ProviderFactory providerFactory = new ProviderFactory();

    private void write(Database db) throws IOException, DatabaseException {
        objectMapper.registerModule(new JSR310Module());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        for (Shop s : providerFactory.getShops()) {
            List<Fetch> fetches = db.getFetches(s);
            if (fetches == null) {
                System.out.println("Data for shop " + s.getTitle() + " is empty!");
                continue;
            }
            fetches.sort(Comparator.comparing(Fetch::getCreatedDate));

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(s.getTitle() + ".json"), fetches);
            System.out.println("Success export: " + s.getTitle() + "--");
        }
    }

    public static void main(String[] args) throws IOException, DatabaseException {
        Database db = new Database("database.db");

        ExportToJsonFile exportToJsonFile = new ExportToJsonFile();
        exportToJsonFile.write(db);
    }
}
