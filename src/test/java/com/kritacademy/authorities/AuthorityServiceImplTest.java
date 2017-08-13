package com.kritacademy.authorities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by chertpong.github.io on 1/3/2017.
 */
@RunWith(SpringRunner.class)
public class AuthorityServiceImplTest {
    @MockBean
    private AuthorityRepository authorityRepository;
    @Test
    public void findAll() throws Exception {
        AuthorityServiceImpl authorityService = new AuthorityServiceImpl(authorityRepository);
        authorityService.findAll();
        verify(authorityRepository, times(1)).findAll();
    }

    @Test
    public void findOneByName() throws Exception {
        AuthorityServiceImpl authorityService = new AuthorityServiceImpl(authorityRepository);
        authorityService.findOneByName("ROLE_ADMIN");
        verify(authorityRepository, times(1)).findOneByName("ROLE_ADMIN");
    }

    @Test
    public void findOneById() throws Exception {
        AuthorityServiceImpl provinceService = new AuthorityServiceImpl(authorityRepository);
        provinceService.findOneById(1);
        verify(authorityRepository, times(1)).findOne(1);
    }

    @Test
    public void create() throws Exception {
        AuthorityServiceImpl authorityService = new AuthorityServiceImpl(authorityRepository);
        authorityService.create(new Authority());
        verify(authorityRepository, times(1)).save(new Authority());
    }

    @Test
    public void update() throws Exception {
        AuthorityServiceImpl authorityService = new AuthorityServiceImpl(authorityRepository);
        authorityService.update(new Authority());
        verify(authorityRepository, times(1)).save(new Authority());
    }

    @Test
    public void delete() throws Exception {
        AuthorityServiceImpl authorityService = new AuthorityServiceImpl(authorityRepository);
        authorityService.delete(1);
        verify(authorityRepository, times(1)).delete(1);
    }

}