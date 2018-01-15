package ru.proshik.applepriceparcer.model;

public class CallbackInfo {

    private String id;
    private String value;

    public CallbackInfo() {
    }

    public CallbackInfo(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
