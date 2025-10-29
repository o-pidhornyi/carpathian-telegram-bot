package com.carpathian.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Carpathian bot application.  Enabling
 * scheduling allows re‑engagement tasks to be executed at fixed
 * intervals (e.g., weekly editorials).  All other configuration is
 * handled via Spring Boot auto‑configuration and property files.
 */
@SpringBootApplication
@EnableScheduling
public class CarpathianBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarpathianBotApplication.class, args);
    }
}