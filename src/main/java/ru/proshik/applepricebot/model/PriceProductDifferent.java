package ru.proshik.applepricebot.model;

import ru.proshik.applepricebot.repository.model.Product;

import java.math.BigDecimal;

public class PriceProductDifferent {

    private Product product;

    private BigDecimal prevPrice;

    private BigDecimal currPrice;

    public PriceProductDifferent(Product product, BigDecimal prevPrice, BigDecimal currPrice) {
        this.product = product;
        this.prevPrice = prevPrice;
        this.currPrice = currPrice;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getPrevPrice() {
        return prevPrice;
    }

    public BigDecimal getCurrPrice() {
        return currPrice;
    }
}
