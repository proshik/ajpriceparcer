package ru.proshik.applepriceparcer.model;

import java.util.function.Supplier;

public class CachedValue<T> {

    volatile private long dateOfBirth = 0L;
    private long timeToLive = 0L;
    private T value = null;

    public CachedValue(Long timeToLive) {
        this.timeToLive = timeToLive;
    }

    private T update(T value) {
        this.value = value;
        this.dateOfBirth = System.currentTimeMillis();
        return value;
    }

    public T get(Supplier<T> supplier) {
        return (dateOfBirth == 0L || dateOfBirth + timeToLive < System.currentTimeMillis())
                ? update(supplier.get())
                : value;
    }

}