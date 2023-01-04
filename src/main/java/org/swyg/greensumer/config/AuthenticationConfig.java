package org.swyg.greensumer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.swyg.greensumer.config.filter.JwtTokenFilter;
import org.swyg.greensumer.exception.CustomAuthenticationEntryPoint;
import org.swyg.greensumer.service.UserService;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    @Value("${jwt.secret-key}")
    private String key;

    private final UserService userService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .regexMatchers("^(?!/api/).*")
                .antMatchers(HttpMethod.POST, "/api/*/users/sign-up", "/api/*/users/login", "/api/*/users/mail")
                .antMatchers(HttpMethod.GET, "/api/*/users/{username}", "/api/*/users/find/username", "/api/v1/posts")
                .antMatchers(HttpMethod.DELETE, "/api/*/users/mail")
                .antMatchers(HttpMethod.PUT, "/api/*/users/find/password")
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/**").authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
    }

}
