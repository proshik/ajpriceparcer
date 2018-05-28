package ru.proshik.applepricebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.proshik.applepricebot.repository.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


}
