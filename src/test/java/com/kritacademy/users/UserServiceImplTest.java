package com.kritacademy.users;

import com.kritacademy.authorities.Authority;
import com.kritacademy.authorities.AuthorityService;
import com.kritacademy.exceptions.DataNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by chertpong.github.io on 1/3/2017.
 */
@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AuthorityService authorityService;

    @Test
    public void findAll() throws Exception {
        given(userRepository.findAll()).willReturn(Collections.emptyList());
        UserServiceImpl userService = new UserServiceImpl(userRepository, authorityService);
        List<User> userList = userService.findAll();
        assertThat(userList).hasSize(0);
        verify(userRepository, times(1)).findAll();
    }


    @Test
    public void findOneByEmail() throws Exception {
        User testUser = new User();
        testUser.setEmail("test@test.com");
        given(userRepository.findOneByEmail(testUser.getEmail())).willReturn(Optional.of(testUser));

        UserServiceImpl userService = new UserServiceImpl(userRepository, authorityService);
        Optional<User> result = userService.findOneByEmail("test@test.com");
        assertThat(result).isEqualTo(Optional.of(testUser));
        verify(userRepository, times(1)).findOneByEmail("test@test.com");

        given(userRepository.findOneByEmail(any())).willReturn(Optional.empty());
        Optional<User> notFoundUser = userService.findOneByEmail("haha@fmail.com");
        assertThat(notFoundUser.isPresent()).isEqualTo(false);
        verify(userRepository, times(1)).findOneByEmail("haha@fmail.com");
    }

    @Test
    public void findOneById() throws Exception {
        UserServiceImpl userService = new UserServiceImpl(userRepository, authorityService);
        userService.findOneById(1);
        verify(userRepository, times(1)).findOne(1);
    }

    @Test
    public void create() throws Exception {
        User user = new User();
        UserServiceImpl userService = new UserServiceImpl(userRepository, authorityService);
        userService.create(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void update() throws Exception {
        User user = new User();
        UserServiceImpl userService = new UserServiceImpl(userRepository, authorityService);
        userService.update(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void delete() throws Exception {
        UserServiceImpl userService = new UserServiceImpl(userRepository, authorityService);
        userService.delete(1);
        verify(userRepository, times(1)).delete(1);
    }

    @Test
    public void promoteUserByIdShouldPassWhenUserAndAuthorityAreExist() throws Exception {
        Authority roleAdmin = new Authority();
        roleAdmin.setId(1);
        roleAdmin.setName("ROLE_ADMIN");

        Authority roleUser = new Authority();
        roleUser.setId(2);
        roleUser.setName("ROLE_USER");

        User user = new User();
        user.setId(1);
        user.setFirstName("user one");
        user.setEmail("user01@kritacademy.com");
        user.getAuthorities().add(roleUser);
        user.setPassword("1234");

        User user2 = new User();
        user2.setId(2);
        user2.setFirstName("user two");
        user2.setEmail("user02@kritacademy.com");
        user2.getAuthorities().add(roleUser);
        user2.setPassword("1234");

        given(authorityService.findOneByName("ROLE_ADMIN")).willReturn(Optional.of(roleAdmin));
        given(userRepository.findOne(1)).willReturn(user);
        given(userRepository.save(user)).will(invocationOnMock -> {
            User u = invocationOnMock.getArgumentAt(0, User.class);
            u.getAuthorities().add(roleAdmin);
            return u;
        });
        UserServiceImpl userService = new UserServiceImpl(userRepository, authorityService);
        User promotedUser = userService.promoteUserById(1, "ROLE_ADMIN");
        assertThat(promotedUser.getAuthorities()).contains(roleAdmin);
        verify(userRepository, times(1)).findOne(1);
        verify(authorityService,times(1)).findOneByName("ROLE_ADMIN");
    }

    @Test
    public void promoteUserByIdShouldFailWhenUserIsNotExist() throws Exception {
        given(userRepository.findOne(1)).willReturn(null);
        UserServiceImpl userService = new UserServiceImpl(userRepository, authorityService);
        try {
            User promotedUser = userService.promoteUserById(1, "ROLE_ADMIN");
        }
        catch (DataNotFoundException e) {
            assertThat(e).isNotNull();
            verify(userRepository, times(1)).findOne(1);
            verify(authorityService,times(0)).findOneByName("ROLE_ADMIN");
        }
    }

    @Test
    public void promoteUserByIdShouldFailWhenUserIsExistButAuthorityNot() throws Exception {
        Authority roleUser = new Authority();
        roleUser.setId(2);
        roleUser.setName("ROLE_USER");

        User user = new User();
        user.setId(1);
        user.setFirstName("user one");
        user.setEmail("user01@kritacademy.com");
        user.getAuthorities().add(roleUser);
        user.setPassword("1234");

        given(authorityService.findOneByName("ROLE_ADMIN")).willReturn(Optional.empty());
        given(userRepository.findOne(1)).willReturn(user);
        UserServiceImpl userService = new UserServiceImpl(userRepository, authorityService);
        try {
            User promotedUser = userService.promoteUserById(1, "ROLE_ADMIN");
        }
        catch (DataNotFoundException e) {
            assertThat(e).isNotNull();
            verify(userRepository, times(1)).findOne(1);
            verify(authorityService,times(1)).findOneByName("ROLE_ADMIN");
        }
    }

    @Test
    public void demoteUserByIdShouldSuccessWhenUserIsFound() throws Exception {
        Authority roleAdmin = new Authority();
        roleAdmin.setId(1);
        roleAdmin.setName("ROLE_ADMIN");

        Authority roleUser = new Authority();
        roleUser.setId(2);
        roleUser.setName("ROLE_USER");

        User user = new User();
        user.setId(1);
        user.setFirstName("user one");
        user.setEmail("user01@kritacademy.com");
        user.getAuthorities().addAll(Arrays.asList(roleUser, roleAdmin));
        user.setPassword("1234");

        given(userRepository.findOne(1)).willReturn(user);
        given(userRepository.save(user)).will(invocationOnMock -> invocationOnMock.getArgumentAt(0, User.class));
        UserServiceImpl userService = new UserServiceImpl(userRepository, authorityService);
        User promotedUser = userService.demoteUserById(1, "ROLE_ADMIN");
        assertThat(promotedUser.getAuthorities()).doesNotContain(roleAdmin);
        verify(userRepository, times(1)).findOne(1);
    }

    @Test
    public void demoteUserByIdShouldFailWhenUserIsNotExist() throws Exception {
        given(userRepository.findOne(1)).willReturn(null);
        UserServiceImpl userService = new UserServiceImpl(userRepository, authorityService);
        try {
            User promotedUser = userService.demoteUserById(1, "ROLE_ADMIN");
        }
        catch (DataNotFoundException e) {
            assertThat(e).isNotNull();
            verify(userRepository, times(1)).findOne(1);
            verify(authorityService,times(0)).findOneByName("ROLE_ADMIN");
        }
    }
}
