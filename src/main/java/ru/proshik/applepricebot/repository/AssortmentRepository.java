package ru.proshik.applepricebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssortmentRepository extends JpaRepository<Assortment, Long> {

    @Query("select a from Assortment a join fetch a.provider provider " +
            "where a.fetchDate >= :startFetchDate and a.fetchDate < :endFetchDate and a.fetchType=:fetchType")
    List<Assortment> findByFetchDateAndFetchType(@Param("startFetchDate") LocalDateTime startOfDay,
                                                 @Param("endFetchDate") LocalDateTime endOfDay,
                                                 @Param("fetchType") FetchType fetchType);
}
