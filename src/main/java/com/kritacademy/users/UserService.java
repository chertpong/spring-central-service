package com.kritacademy.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
public interface UserService {
    List<User> findAll();
    Page<User> findAllWithAuthorities(Pageable pageable);
    Optional<User> findOneByEmail(String email);
    Optional<User> findOneByFacebookId(BigInteger id);
    User findOneById(Integer id);
    User create(User user);
    User update(User user);
    void delete(Integer id);
    User promoteUserById(Integer id, String authorityName);
    User demoteUserById(Integer id, String authorityName);
}
