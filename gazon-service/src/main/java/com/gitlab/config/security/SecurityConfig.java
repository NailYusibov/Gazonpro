package com.gitlab.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Profile("security")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:8180/realms/GazonRealm/protocol/openid-connect/certs").build();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .antMatchers("/api/bank-card/**").hasRole("ADMIN")
                        .antMatchers("/api/personal-address/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthConverter);
        http
                .sessionManagement()
                .sessionCreationPolicy(STATELESS);
        http.cors().and().csrf().disable();

        return http.build();
    }
}
