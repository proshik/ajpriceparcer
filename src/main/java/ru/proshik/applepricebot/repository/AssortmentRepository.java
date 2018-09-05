package ru.proshik.applepricebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.ProviderType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssortmentRepository extends CustomAssortmentRepository, JpaRepository<Assortment, Long>, JpaSpecificationExecutor<Assortment> {

    @Query("select a from Assortment a join fetch a.provider provider join fetch a.products product " +
            "where a.fetchDate >= :startFetchDate " +
            "and a.fetchDate < :endFetchDate " +
            "and a.fetchType=:fetchType " +
            "and provider.type = :providerType " +
            "and product.productType = :productType")
    List<Assortment> findByFetchDateAndFetchType(@Param("startFetchDate") LocalDateTime startOfDay,
                                                 @Param("endFetchDate") LocalDateTime endOfDay,
                                                 @Param("fetchType") FetchType fetchType,
                                                 @Param("providerType") ProviderType providerType,
                                                 @Param("productType") ProductType productType);
}
