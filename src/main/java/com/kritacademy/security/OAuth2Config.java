package com.kritacademy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@Configuration
public class OAuth2Config {
    private static final String RESOURCE_ID = "kritacademy";

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(RESOURCE_ID).stateless(true).tokenStore(tokenStore);
        }
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .headers().frameOptions().disable()
                    .and()
                    .requestMatchers().antMatchers("/user","/me", "/v1/api/**")
                    .and()
                    .authorizeRequests()
//                    .antMatchers("/user","/me").authenticated()
//                    .antMatchers("/oauth/token**").permitAll()
//                    .antMatchers("/oauth/authorize**").permitAll()
//                    .antMatchers("/v1/api/**").permitAll()
                    .anyRequest().permitAll();
        }

    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Autowired
        private UserApprovalHandler userApprovalHandler;

        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

            // @formatter:off
            clients
                    .inMemory().withClient("kritacademy-mobile")
                    .resourceIds(RESOURCE_ID)
                    .authorizedGrantTypes("implicit", "password", "refresh_token", "client_credentials")
                    .authorities("ROLE_CLIENT")
                    .scopes("read", "write", "trust")
                    .secret("kritacademy-mobile-secret")
                    .accessTokenValiditySeconds(3600)
                    .and()
                    .withClient("kritacademy-web")
                    .resourceIds(RESOURCE_ID)
                    .authorizedGrantTypes("implicit")
                    .authorities("ROLE_CLIENT")
                    .scopes("read", "write", "trust")
                    .secret("kritacademy-web-secret")
                    .accessTokenValiditySeconds(3600)
                    .autoApprove(true)
                    .redirectUris("http://localhost:3000");
            // @formatter:on
        }

        @Bean
        public TokenStore tokenStore() {
            return new InMemoryTokenStore();
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
                    .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer.realm("kritacademy/client")
                    .checkTokenAccess("permitAll()");
        }

//        @Primary
//        @Bean
//        public DefaultTokenServices defaultTokenServices(ClientDetailsService clientDetailsService) {
//            DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//            defaultTokenServices.setTokenStore(tokenStore);
//            defaultTokenServices.setSupportRefreshToken(true);
//            defaultTokenServices.setClientDetailsService(clientDetailsService);
//            return defaultTokenServices;
//        }
    }

    protected static class Stuff {

        @Autowired
        private ClientDetailsService clientDetailsService;

        @Autowired
        private TokenStore tokenStore;

        @Bean
        public ApprovalStore approvalStore() throws Exception {
            TokenApprovalStore store = new TokenApprovalStore();
            store.setTokenStore(tokenStore);
            return store;
        }

        @Bean
        @Lazy
        @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
        public com.kritacademy.security.UserApprovalHandler userApprovalHandler() throws Exception {
            com.kritacademy.security.UserApprovalHandler handler = new com.kritacademy.security.UserApprovalHandler();
            handler.setApprovalStore(approvalStore());
            handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
            handler.setClientDetailsService(clientDetailsService);
            handler.setUseApprovalStore(true);
            return handler;
        }
    }
}
