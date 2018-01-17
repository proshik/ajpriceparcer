package ru.proshik.applepriceparcer.storage;

import ru.proshik.applepriceparcer.exception.DatabaseException;

public interface Repository<K, V> {

    void add(K key, V value) throws DatabaseException;
}
