package ru.proshik.applepricebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.proshik.applepricebot.repository.model.Product;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByFetchDate(LocalDateTime fetchDate);
}
