package org.swyg.greensumer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.swyg.greensumer.config.filter.JwtTokenFilter;
import org.swyg.greensumer.exception.CustomAuthenticationEntryPoint;
import org.swyg.greensumer.service.UserEntityRepositoryService;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class AuthenticationConfig {

    @Value("${jwt.secret-key}")
    private String key;

    private final UserEntityRepositoryService userEntityRepositoryService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers("/api/users/**").permitAll()
                        .mvcMatchers("/api/reviews").authenticated()
                        .mvcMatchers("/api/images").authenticated()
                        .mvcMatchers("/api/interviews").hasRole("ADMIN")
                        .mvcMatchers("/api/stores", "/api/events").hasRole("SELLER")
                        .antMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()
                        .antMatchers(HttpMethod.GET, "/api/reviews", "/api/reviews/news").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/stores", "/api/stores/*/products", "/api/stores/*/products/{productId:[\\\\d+]}").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/events", "/api/events/news", "/api/interviews").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(h -> h.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtTokenFilter(key, userEntityRepositoryService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .build();
    }

}
