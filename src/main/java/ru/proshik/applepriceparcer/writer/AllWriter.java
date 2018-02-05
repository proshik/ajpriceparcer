package ru.proshik.applepriceparcer.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model2.Fetch;
import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.provider2.ProviderFactory;
import ru.proshik.applepriceparcer.storage.Database2;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class AllWriter {

    private ObjectMapper objectMapper = new ObjectMapper();

    private ProviderFactory providerFactory = new ProviderFactory();

    private void write(Database2 db) throws IOException, DatabaseException {
        objectMapper.registerModule(new JSR310Module());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        for (Shop s : providerFactory.getShops()) {
            List<Fetch> fetches = db.getFetches(s);
            fetches.sort(Comparator.comparing(Fetch::getCreatedDate));

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(s.getTitle() + ".json"), fetches);
            System.out.println("--Done--" + s.getTitle() + "--");
        }
    }

    public static void main(String[] args) throws IOException, DatabaseException {
        Database2 db = new Database2("data/database1.db");

        AllWriter allWriter = new AllWriter();

        allWriter.write(db);
    }
}
