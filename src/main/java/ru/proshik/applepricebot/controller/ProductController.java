package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ShopType;
import ru.proshik.applepricebot.service.v2.ProductService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("product")
    public Map<ShopType, List<Product>> products() {
        return productService.provideProducts();
    }

}
