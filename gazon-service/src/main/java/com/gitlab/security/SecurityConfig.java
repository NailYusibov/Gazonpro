package com.gitlab.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if (true) {
            http.csrf().disable()
                    .authorizeHttpRequests()
                    .anyRequest()
                    .authenticated();

            http.oauth2ResourceServer().jwt();

            http.sessionManagement().sessionCreationPolicy(STATELESS);
        } else {
            http
                    .authorizeRequests()
                    .anyRequest().permitAll();
        }

        return http.build();
    }
}
