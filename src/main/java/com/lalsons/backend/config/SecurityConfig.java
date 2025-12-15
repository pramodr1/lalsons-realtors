package com.lalsons.backend.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	

    @Value("${app.security.enabled:true}")
    private boolean securityEnabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable());

        if (securityEnabled) {
            http.authorizeHttpRequests(auth -> auth
                // Public Endpoints (Search, Health, View)
                .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                .requestMatchers("/uploads/**").permitAll() // Allow image access

                // Bulk Upload: ADMIN Only
                .requestMatchers("/api/admin/properties/upload").hasRole("ADMIN")

                // Create/Edit/Delete: ADMIN or SELLER
                .requestMatchers("/api/admin/properties/**").hasAnyRole("ADMIN", "SELLER")

                // Deny all else
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults()); // Use Basic Auth (Popup)
        } else {
            // Security Disabled
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }

        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // This enables {noop} passwords
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        
    }
}