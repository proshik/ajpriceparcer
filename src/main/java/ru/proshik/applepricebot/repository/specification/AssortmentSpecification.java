package ru.proshik.applepricebot.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.ProviderType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssortmentSpecification {

    public static Specification<Assortment> filter(LocalDateTime startOfDay,
                                                   LocalDateTime endOfDay,
                                                   FetchType fetchType,
                                                   ProviderType providerType,
                                                   ProductType productType) {

        return new Specification<Assortment>() {
            @Override
            public Predicate toPredicate(Root<Assortment> root,
                                         CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                root.fetch("products");
                root.fetch("provider");
                criteriaQuery.distinct(true);

                if (startOfDay != null && endOfDay != null) {
                    predicates.add(criteriaBuilder.between(root.get("fetchDate"), startOfDay, endOfDay));
                }
                if (fetchType != null) {
                    predicates.add(criteriaBuilder.equal(root.get("fetchType"), fetchType));
                }
                if (providerType != null) {
                    predicates.add(criteriaBuilder.equal(root.get("provider").get("type"), providerType));
                }
                // First attempt get products by productType
//                if (productType != null) {
//                    predicates.add(criteriaBuilder.equal(products.get("productType"), productType));
//                }

                // Second attempt get products by productType
//                Root<Product> producdts = criteriaQuery.from(Product.class);
//
//                Path<ProductType> productTypePath = producdts.get("productType");
//
//                Predicate customerIsAccountOwner = criteriaBuilder.equal(producdts.<Product>get("assortment"), root);
//                Predicate accountExpiryDateBefore = criteriaBuilder.equal(productTypePath, productType);
//
//                predicates.add(customerIsAccountOwner);
//                predicates.add(accountExpiryDateBefore);

                Predicate[] predicatesArray = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(predicatesArray));
            }
        };
    }

}