package ru.proshik.applepricebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.proshik.applepricebot.repository.model.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    @Modifying
    @Transactional
    @Query("update Provider set enabled = :enabled where id = :providerId")
    int updateEnableValue(@Param("providerId") Long providerId,
                          @Param("enabled") boolean enabled);
}
