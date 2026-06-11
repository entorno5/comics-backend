package com.comics.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application configuration class.
 * Defines beans for password encoding and API documentation.
 */
@Configuration
public class AppConfig {

    /**
     * Password encoder using BCrypt algorithm.
     * BCrypt automatically handles salting and provides strong security.
     * 
     * Strength: 12 iterations (default) provides good balance between security and performance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * OpenAPI (Swagger 3.0) documentation configuration.
     * Generates interactive API documentation accessible at /swagger-ui.html
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Comics Backend API")
                        .description("RESTful API for Comics Management Application")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@comics.com")
                                .url("https://comics.example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
