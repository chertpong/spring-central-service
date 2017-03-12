package com.kritacademy.authorities;

import java.util.List;
import java.util.Optional;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
public interface AuthorityService {
    List<Authority> findAll();
    Optional<Authority> findOneByName(String name);
    Authority findOneById(Integer id);
    Authority create(Authority authority);
    Authority update(Authority authority);
    void delete(Integer id);
}
