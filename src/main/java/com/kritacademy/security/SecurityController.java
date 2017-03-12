package com.kritacademy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@Controller
public class SecurityController {
    private final TokenStore tokenStore;

    @Autowired
    public SecurityController(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping({ "/user", "/me" })
    public @ResponseBody Map<String, Object> user(Authentication authentication) {
        Map<String, Object> map = new LinkedHashMap<>();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        map.put("username", userDetails.getUsername());
        map.put("email", userDetails.getUsername());
        map.put("authorities", userDetails.getAuthorities().toArray());
        return map;
    }

    @PostMapping("/oauth/token/revoke")
    public @ResponseBody void singleLogout(@RequestParam("token") String token){
        tokenStore.removeAccessToken(tokenStore.readAccessToken(token));
    }

}
