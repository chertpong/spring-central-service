package com.kritacademy.security;

import com.kritacademy.users.User;
import com.kritacademy.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService{
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userService.findOneByEmail(email);
        return user
                .map(u -> {
                    List<GrantedAuthority> authorities = u.getAuthorities()
                            .stream()
                            .map(authority -> {
                                return new SimpleGrantedAuthority(authority.getName());
                            })
                            .collect(Collectors.toList());
                    return new org.springframework.security.core.userdetails.User(email, u.getPassword(), authorities);
                })
                .orElseThrow(() -> new UsernameNotFoundException("No users email " + email + "found"));
    }
}
