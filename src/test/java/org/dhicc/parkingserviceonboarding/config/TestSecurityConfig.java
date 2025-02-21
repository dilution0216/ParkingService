package org.dhicc.parkingserviceonboarding.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/me").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("ROLE_ADMIN") // 🔥 테스트에서도 적용
                        .anyRequest().authenticated()
                )
                .build();
    }
}

