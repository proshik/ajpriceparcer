package ru.proshik.applepricesbot.dto;

import ru.proshik.applepricesbot.storage.model.Product;

public class DiffProducts {

    private Product oldProductDesc;
    private Product newProductDesc;

    public DiffProducts(Product oldProductDesc, Product newProductDesc) {
        this.oldProductDesc = oldProductDesc;
        this.newProductDesc = newProductDesc;
    }

    public Product getOldProductDesc() {
        return oldProductDesc;
    }

    public Product getNewProductDesc() {
        return newProductDesc;
    }
}
