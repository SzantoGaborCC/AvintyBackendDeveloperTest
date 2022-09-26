package com.jwt_test.app.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Profile("NO_AUTH")
@EnableWebSecurity
public class NoAuthSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return  // Disable CSRF
                http.csrf().disable()
                        // Permit all requests without authentication
                        .authorizeRequests().anyRequest().permitAll()
                        .and().cors()
                        .and()
                        .headers().frameOptions().disable()
                        .and()
                        .build();
    }
}
