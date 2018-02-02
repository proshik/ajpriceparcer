package ru.proshik.applepriceparcer.model2.result;

import ru.proshik.applepriceparcer.model2.Product;

import java.time.LocalDateTime;
import java.util.List;

public class ReadResult {

    private LocalDateTime createdDate;
    private List<Product> products;

    public ReadResult(LocalDateTime createdDate, List<Product> products) {
        this.createdDate = createdDate;
        this.products = products;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public List<Product> getProducts() {
        return products;
    }
}
