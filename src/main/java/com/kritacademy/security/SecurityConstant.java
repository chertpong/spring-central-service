package com.kritacademy.security;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
public class SecurityConstant {
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ALLOW_ONLY_ADMIN = "hasRole('" + ROLE_ADMIN + "')";
    public static final String ALLOW_AUTHENTICATED = "isAuthenticated()";
}
