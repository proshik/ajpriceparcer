package ru.proshik.applepriceparcer.storage;

import ru.proshik.applepriceparcer.exception.DatabaseException;

public class CrudRepository<K, V> implements Repository<K, V> {

//    public void addAssortment(Shop shop, Assortment assortment) throws DatabaseException {
//        DB db = open();
//
//        HTreeMap<Shop, String> map = createOrOpenShopBucket(db);
//        String assortmentString = map.get(shop);
//
//        List<Assortment> assortments = new ArrayList<>();
//        if (assortmentString != null) {
//            try {
//                Assortment[] assortmentsArray = mapper.readValue(assortmentString, Assortment[].class);
//
//                if (assortmentsArray != null) {
//                    assortments = Stream.of(assortmentsArray).collect(Collectors.toList());
//                }
//            } catch (IOException e) {
//                throw new DatabaseException(e);
//            } finally {
//                db.close();
//            }
//        }
//
//        assortments.add(assortment);
//
//        try {
//            String updatedAssortmentString = mapper.writeValueAsString(assortments);
//            map.put(shop, updatedAssortmentString);
//        } catch (JsonProcessingException e) {
//            throw new DatabaseException(e);
//        } finally {
//            db.close();
//        }
//    }

    @Override
    public void add(K key, V value) throws DatabaseException {

    }
}
