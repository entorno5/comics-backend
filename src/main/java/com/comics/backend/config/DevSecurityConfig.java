package com.comics.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Security configuration for the application.
 * Configures HTTP security, CORS, and session management.
 * 
 * NOTE: This configuration allows all requests for development purposes.
 * In production, implement proper authentication and authorization.
 */
@Configuration
@EnableWebSecurity
public class DevSecurityConfig {

    /**
     * Security filter chain configuration.
     * Currently allows all requests for development.
     * Should be enhanced with JWT/OAuth2 for production.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless API
                .csrf(csrf -> csrf.disable())
                
                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Set session management to stateless (required for JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to Swagger/OpenAPI documentation
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        
                        // Allow public access to actuator health endpoint
                        .requestMatchers("/actuator/health").permitAll()
                        
                        // All other requests require authentication
                        // In development, allowing all. In production, configure properly.
                        .anyRequest().permitAll()
                        
                        // Uncomment for production-like security:
                        // .requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                        // .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        // .requestMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
                        // .requestMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
                        // .requestMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()
                        // .anyRequest().authenticated()
                );
        
        return http.build();
    }

    /**
     * CORS configuration.
     * Allows requests from any origin with common methods.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow all origins (in production, specify exact origins)
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Allow common headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Set max age for preflight cache
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
