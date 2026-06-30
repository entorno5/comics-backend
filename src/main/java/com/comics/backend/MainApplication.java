package com.comics.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Main Spring Boot Application class for the Comics Backend.
 * 
 * This application provides RESTful APIs for managing:
 * - Users (user accounts, authentication)
 * - Comics (comic books inventory and information)
 * 
 * Technologies:
 * - Spring Boot 3.5.10
 * - Spring Security
 * - MongoDB
 * - JWT (when fully implemented)
 * - Swagger/OpenAPI for documentation
 * 
 * API Documentation: http://localhost:8080/swagger-ui.html
 * Health Check: http://localhost:8080/actuator/health
 */
@SpringBootApplication
@Slf4j
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        log.info("╔════════════════════════════════════════╗");
        log.info("║   Comics Backend Started Successfully  ║");
        log.info("║   API Docs: /swagger-ui.html          ║");
        log.info("║   Health: /actuator/health            ║");
        log.info("╚════════════════════════════════════════╝");
    }
}
