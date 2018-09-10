package ru.proshik.applepricebot.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query(value = "select distinct u from User u join fetch u.subscriptions s join fetch s.provider p")
    List<User> findUsers(Pageable pageable);

//    @Query(value = "select u from User u inner join fetch u.subscriptions s")
//    Page<User> findUsers(Pageable pageable);
}
