package com.kritacademy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
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
    public @ResponseBody Object user(Authentication authentication) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            map.put("details", oAuth2Authentication.getUserAuthentication().getDetails());
            map.put("authorities", oAuth2Authentication.getAuthorities().toArray());
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Map<String, String> details = new HashMap<>();
            details.put("email", userDetails.getUsername());
            map.put("details", details);
            map.put("authorities", userDetails.getAuthorities());
        }
        return map;
    }

    @PostMapping("/oauth/token/revoke")
    public @ResponseBody void singleLogout(@RequestParam("token") String token){
        tokenStore.removeAccessToken(tokenStore.readAccessToken(token));
    }

}
