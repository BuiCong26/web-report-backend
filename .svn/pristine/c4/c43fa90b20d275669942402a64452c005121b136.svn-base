package com.sm.base;



import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

// causes Lombok to generate a logger field.
@Slf4j
// serves two purposes i.e. configuration and bootstrapping.
@SpringBootApplication
@EnableScheduling
public class Runner  extends SpringBootServletInitializer {

  @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Runner.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(Runner.class, args);
        log.info("Application started successfully.");
    }
}
