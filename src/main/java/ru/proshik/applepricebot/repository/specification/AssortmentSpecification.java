package ru.proshik.applepricebot.repository.specification;

import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.springframework.data.jpa.domain.Specification;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.ProviderType;

import javax.persistence.criteria.*;
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

                root.fetch("provider");
//                root.fetch("products");
                Join<Object, Object> products = root.join("products");
//                roo.fetch((PluralAttributePath) root.get("products"));

                criteriaQuery.distinct(true);
//                root.get(products)
//                root.get("fetchType")

                criteriaQuery.where(criteriaBuilder.equal(root.get("fetchType"), fetchType));
                criteriaQuery.where(criteriaBuilder.equal(products.get("productType"), productType));

                return criteriaQuery.getRestriction();

//                System.out.println();
//                criteriaBuilder.equal(root.g().)
//
//                root.get("adf").
//                        criteriaQuery.distinct(true);
//
//                criteriaQuery.equals()
//
//                root.get("products");
//                products.get("productType")
//                products.on(criteriaBuilder.equal(products.get("productType"), productType));
//                Root<Product> from = criteriaQuery.from(Product.class);

//                Path<Product> products1 = root.get("products");
//                products1.get("productType");
//                if (startOfDay != null && endOfDay != null) {
//                    predicates.add(criteriaBuilder.between(root.get("fetchDate"), startOfDay, endOfDay));
//                }
//                if (fetchType != null) {
//                    predicates.add(criteriaBuilder.equal(root.get("fetchType"), fetchType));
//                }
//                if (providerType != null) {
//                    predicates.add(criteriaBuilder.equal(root.get("provider").get("type"), providerType));
//                }
//                if (productType != null) {
//                    Path<Object> objectPath = root.join("products").get("productType");
//                    Join<Assortment, Product> assortmentProductJoin = root.join("products");
//                    assortmentProductJoin.fetch("")
//                    Path<Object> productType1 = assortmentProductJoin.get("productType");
//                    Join<Assortment, Product> on = assortmentProductJoin.on(criteriaBuilder.equal(assortmentProductJoin.get("productType"), productType));
//                    predicates.add(criteriaBuilder.equal(from.get("productType"), productType));
//                    root.fetch("products");

//                    predicates.add(root.join("products").get("productType").in(productType));
//                }
//                return criteriaBuilder.conjunction();
//                Join<Assortment, Product> company = root.join("products");
//
//                criteriaQuery.where(criteriaBuilder.equal(company.get("productType"), productType));
//                return criteriaQuery.getGroupRestriction();
//                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//                return criteriaBuilder.equal(company.get("productType"), productType);
            }
        };
    }

//    private static Specification<Product> productSpecification(ProductType productType) {
//        return new Specification<Product>() {
//            @Override
//            public Predicate toPredicate(Root<Product> root,
//                                         CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                return null;
//            }
//        }
//    }
}