package ru.proshik.applepricebot.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.Subscription;
import ru.proshik.applepricebot.repository.model.User;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionSpecification {


    public static Specification<Subscription> filter(Long userId, ProductType productType) {

        return new Specification<Subscription>() {
            @Override
            public Predicate toPredicate(Root<Subscription> root,
                                         CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                root.fetch("provider");
                root.fetch("user");
                criteriaQuery.distinct(true);

                if (userId != null) {
                    Path<User> user = root.get("user");
                    predicates.add(criteriaBuilder.equal(user.get("id"), userId));
                }
                predicates.add(criteriaBuilder.equal(root.get("productType"), productType));

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

}
