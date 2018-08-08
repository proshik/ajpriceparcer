package ru.proshik.applepricebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.proshik.applepricebot.repository.model.Assortment;

@Repository
public interface AssortmentRepository extends JpaRepository<Assortment, Long> {

}
