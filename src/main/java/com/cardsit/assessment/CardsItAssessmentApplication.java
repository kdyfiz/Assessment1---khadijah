package com.cardsit.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point for the Cards IT Assessment API.
 *
 * <p>Bootstraps Spring Boot with component scanning under
 * {@code com.cardsit.assessment} (controllers, services, repositories, config).
 *
 * <p><b>How to run:</b> {@code mvn spring-boot:run} (ensure MSSQL is up via Docker first).
 * See {@code README.md} for full setup steps.
 */
@SpringBootApplication
public class CardsItAssessmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardsItAssessmentApplication.class, args);
    }
}
