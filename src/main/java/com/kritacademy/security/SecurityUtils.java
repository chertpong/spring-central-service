package com.kritacademy.security;

import com.kritacademy.exceptions.DataNotFoundException;
import com.kritacademy.users.User;
import com.kritacademy.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@Component
public class SecurityUtils {
    @Autowired
    private UserService userService;
    public static String getCurrentUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            throw new DataNotFoundException("User is not logged in");
        }
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }

    public User getCurrentUser() {
        Optional<User> currentUser = userService.findOneByEmail(SecurityUtils.getCurrentUsername());
        if(currentUser.isPresent()){
            return currentUser.get();
        }
        throw new DataNotFoundException("No users email " + SecurityUtils.getCurrentUsername() + "found");
    }
}
