package ru.proshik.applepriceparcer.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model.*;
import ru.proshik.applepriceparcer.storage.Database;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class AjReaderJson {

    private ObjectMapper objectMapper = new ObjectMapper();

    private void read(Database db) throws IOException, DatabaseException {
        objectMapper.registerModule(new JSR310Module());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Path path = Paths.get("aj.json");

        Aj[] ajs = objectMapper.readValue(path.toFile(), Aj[].class);

        System.out.println(ajs);

        Shop s = new Shop("AJ", "http://aj.ru");

        List<Assortment> aj = db.getAssortments(new Shop("AJ", "http://aj.ru"));

        System.out.println(aj);

//        for (Aj a : Arrays.asList(ajs)) {
//
//            List<Product> products = new ArrayList<>();
//
//            for (AjAssortments aja : a.getAssortments()) {
//                String title = aja.getTitle();
//                String description = aja.getDescription();
//
//                List<Item> items = new ArrayList<>();
//
//                for (AjItem aji : aja.getItems()) {
//                    items.add(new Item(aji.getTitle(), aji.getPrice()));
//                }
//
//                products.add(new Product(title, description, items));
//
//            }
//
//            Map<ProductType, List<Product>> productTypeListMap = new HashMap<>();
//            productTypeListMap.put(ProductType.IPHONE, products);
//
//            Assortment assortment = new Assortment(a.getCreatedDate(), productTypeListMap);
//            db.addFetch(s, assortment);
//        }
//
//        aj = db.getAssortments(new Shop("AJ", "http://aj.ru"));
//
//        System.out.println(aj);
    }

    public static void main(String[] args) throws IOException, DatabaseException {

        Database db = new Database("database.db");

        AjReaderJson ajReaderJson = new AjReaderJson();
        ajReaderJson.read(db);
    }

}
