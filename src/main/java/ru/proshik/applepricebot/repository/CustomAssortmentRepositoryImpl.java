package ru.proshik.applepricebot.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.ProviderType;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CustomAssortmentRepositoryImpl implements CustomAssortmentRepository {

    private final EntityManager entityManager;

    @Autowired
    public CustomAssortmentRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Assortment> findAllWithNameOnly(LocalDateTime startOfDay,
                                                LocalDateTime endOfDay,
                                                FetchType fetchType,
                                                ProviderType providerType,
                                                ProductType productType) {

        Query query = entityManager.createQuery("select distinct a from Assortment a " +
                "join fetch a.products product join fetch a.provider provider where product.productType = :productType");
        query.setParameter("productType", productType);

        return query.getResultList();
    }

}
