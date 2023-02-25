package org.swyg.greensumer.config;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.swyg.greensumer.jwt.JwtTokenFilter;
import org.swyg.greensumer.service.UserEntityRepositoryService;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    // private TokenProvider tokenProvider;

    private String key;
    private UserEntityRepositoryService userEntityRepositoryService;

    public JwtSecurityConfig(String key, UserEntityRepositoryService userEntityRepositoryService) {
        this.key = key;
        this.userEntityRepositoryService = userEntityRepositoryService;
    }

    @Override
    public void configure(HttpSecurity http) {
//        http.addFilterBefore(
//                new JwtFilter(tokenProvider),
//                UsernamePasswordAuthenticationFilter.class
//        );

        http.addFilterBefore(
                new JwtTokenFilter(key, userEntityRepositoryService),
                UsernamePasswordAuthenticationFilter.class
        );
    }

}
