package ru.proshik.applepricebot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.proshik.applepricebot.exception.ProductNotFoundException;
import ru.proshik.applepricebot.model.ProductResp;
import ru.proshik.applepricebot.repository.ProductRepository;
import ru.proshik.applepricebot.repository.model.Product;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/product")
public class ProductController {

    private static final Logger LOG = Logger.getLogger(ProductController.class);

    private final ObjectMapper jsonMapper;

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ObjectMapper jsonMapper, ProductRepository productRepository) {
        this.jsonMapper = jsonMapper;
        this.productRepository = productRepository;
    }

    @GetMapping("{productId}")
    public ProductResp provideProduct(@PathVariable(value = "productId") Long productId) {
        return transform(productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found by id=" + productId)));
    }

    private ProductResp transform(Product product) {
        Map<String, String> parameters = new HashMap<>();
//        if (product.getParameters() != null) {
//            try {
//                parameters = jsonMapper.readValue(product.getParameters(), new TypeReference<Map<String, String>>() {
//                });
//            } catch (IOException e) {
//                LOG.error("Unexpected error on parse json value of parameters for product with id=" + product.getId());
//            }
//        }

        return ProductResp.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .available(product.getAvailable())
                .price(product.getPrice())
                .productType(product.getProductType())
                .parameters(parameters)
                .build();
    }

}
