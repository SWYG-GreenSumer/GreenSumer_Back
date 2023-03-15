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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.swyg.greensumer.exception.CustomAuthenticationEntryPoint;
import org.swyg.greensumer.service.UserEntityRepositoryService;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${jwt.secret-key}") private String key;
    // private final TokenProvider tokenProvider;
    private final CorsConfig corsConfig;
    private final UserEntityRepositoryService userEntityRepositoryService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .addFilterBefore(new CorsFilter(corsConfig.corsFilter()), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(security -> security
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .headers(headers -> headers
                        .frameOptions()
                        .sameOrigin()
                )
                .sessionManagement(h -> h.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers("/api/users/**", "/swagger-ui/**", "/v3/**").permitAll()
                        .mvcMatchers("/api/interviews/**").hasRole("ADMIN")
                        .mvcMatchers("/api/stores/**", "api/events").hasRole("SELLER")
                        .antMatchers(HttpMethod.GET, "/api/reviews", "/api/reviews/news", "/api/maps/recommend").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/stores", "/api/stores/*/products", "/api/stores/*/products/{productId:[\\\\d+]}", "/api/images/{imageId:[\\\\d+]}").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/events", "/api/events/news", "/api/interviews").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin().disable()
                .logout().disable()
                .apply(new JwtSecurityConfig(key, userEntityRepositoryService))
                .and()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder encodePassword(){
        return new BCryptPasswordEncoder();
    }

}
