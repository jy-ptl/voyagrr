package com.voyagrr.processingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ProcessingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProcessingServiceApplication.class, args);
    }

}
