package ru.proshik.applepricebot.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ProductType;

public class ProductSpecification {

    public static Specification<Product> byProductType(ProductType productType) {

        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("productType"), productType);
    }
}
