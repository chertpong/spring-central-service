package com.kritacademy.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findOneByEmail(String email);
    @Query(value = "select distinct users from User users left join fetch users.authorities",
            countQuery = "select count(users) from User users")
    Page<User> findAllWithAuthorities(Pageable pageable);

    Optional<User> findOneByFacebookId(BigInteger id);
}
