package com.kritacademy.users;

import com.kritacademy.authorities.Authority;
import com.kritacademy.authorities.AuthorityService;
import com.kritacademy.exceptions.DataAlreadyExists;
import com.kritacademy.exceptions.DataNotFoundException;
import com.kritacademy.security.SecurityConstant;
import com.kritacademy.users.dto.UpdateUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@RestController
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthorityService authorityService;

    @Autowired
    public UserController(PasswordEncoder passwordEncoder, UserService userService, AuthorityService authorityService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.authorityService = authorityService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/v1/api/users")
    public List<User> getAll() {
        return userService.findAll();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/v1/api/users", params = "email")
    public User findOneByEmail(@RequestParam(name = "email") String email){
        return this.userService.findOneByEmail(email)
                .map(u -> u)
                .orElseThrow(() -> new DataNotFoundException("User email " + email + " not found"));
    }

    @GetMapping("/v1/api/users/{id}")
    public User getById(@PathVariable("id") Integer id) {
        return userService.findOneById(id);
    }

    @PostMapping("/v1/api/users")
    public User create(@Valid @RequestBody User user){
        Optional<Authority> userAuthority = authorityService.findOneByName(SecurityConstant.ROLE_USER);
        Optional<User> existingUser = userService.findOneByEmail(user.getEmail());
        if(existingUser.isPresent()) {
            throw new DataAlreadyExists("User email " + user.getEmail() + " is already existed");
        }
        if(userAuthority.isPresent()){
            // add authority
            user.getAuthorities().clear();
            user.getAuthorities().add(userAuthority.get());
            // encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userService.create(user);
        }
        else {
            throw new RuntimeException("Server error, please contact administrator");
        }

    }

    @PutMapping("/v1/api/users/{id}")
    public User update(@PathVariable("id") Integer id, @Valid @RequestBody UpdateUserInfo userInfo){
        Optional<User> oldUser = Optional.ofNullable(userService.findOneById(id));
        User user  = oldUser.map(u -> u).orElseThrow(() -> new DataNotFoundException("User id:" + id + " not found"));
        user.setId(id);
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());

        if(!userInfo.getPassword().isEmpty()) {
            user.setPassword(userInfo.getPassword());
        }
        return userService.update(user);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/v1/api/users/{id}", params = "promote")
    public User promoteUser(@PathVariable("id") Integer id, @RequestParam(name = "promote") String authorityName) {
        return this.userService.promoteUserById(id,authorityName);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/v1/api/users/{id}", params = "demote")
    public User demoteAdmin(@PathVariable("id") Integer id, @RequestParam(name = "demote") String authorityName) {
        return this.userService.demoteUserById(id, authorityName);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/v1/api/users/{id}")
    public void delete(@PathVariable("id") Integer id) {
        userService.delete(id);
    }
}
