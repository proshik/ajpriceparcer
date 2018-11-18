package ru.proshik.applepricebot.service.provider.gsmstore;

import ru.proshik.applepricebot.repository.model.ProductType;

public class ProductTypePointer {

    private ProductType productType;
    private String urlPath;

    ProductTypePointer(ProductType productType, String urlPath) {
        this.productType = productType;
        this.urlPath = urlPath;
    }

    public ProductType getProductType() {
        return productType;
    }

    public String getUrlPath() {
        return urlPath;
    }
}
