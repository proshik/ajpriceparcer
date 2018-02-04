package ru.proshik.applepriceparcer.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model2.*;
import ru.proshik.applepriceparcer.provider2.aj.AjProvider;
import ru.proshik.applepriceparcer.storage.Database2;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AjReaderJson {

    private ObjectMapper objectMapper = new ObjectMapper();

    private void read(Database2 db) throws IOException, DatabaseException {
        objectMapper.registerModule(new JSR310Module());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

//        Path path = Paths.get("aj.json");
//
//        Aj[] ajs = objectMapper.readValue(path.toFile(), Aj[].class);
//
//        System.out.println(ajs);
//
//        Shop s = new Shop("AJ", "http://aj.ru");
//
//        List<Fetch> aj = db.getFetches(new Shop("AJ", "http://aj.ru"));
//
//        System.out.println(aj);
//
//        Map<String, ProductType> collect = Stream.of(ProductType.values())
//                .collect(Collectors.toMap(o -> o.getValue().toUpperCase(), o -> o));
//
//        for (Aj a : Arrays.asList(ajs)) {
//
//            List<Product> products = new ArrayList<>();
//
//            for (AjAssortments aja : a.getAssortments()) {
//                ProductType productType = collect.get(aja.getTitle().toUpperCase());
//                String description = aja.getDescription();
//
//                for (AjItem aji : aja.getItems()) {
//                    products.add(new Product(aji.getTitle(), description, null, aji.getPrice(), AssortmentType.IPHONE,
//                            productType));
//                }
//            }
//
//            Fetch fetch = new Fetch(a.getCreatedDate(), products);
//            db.addFetch(s, fetch);
//        }
//
////        aj = db.getAssortments(new Shop("AJ", "http://aj.ru"));
//
//        System.out.println(aj);

        List<Fetch> fetches = db.getFetches(new Shop(AjProvider.TITLE, AjProvider.URL));

        System.out.println(fetches);
    }

    public static void main(String[] args) throws IOException, DatabaseException {

        Database2 db = new Database2("data/database.db");

        AjReaderJson ajReaderJson = new AjReaderJson();
        ajReaderJson.read(db);
    }

}
