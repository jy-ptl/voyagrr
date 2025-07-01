package com.voyagrr.sharingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SharingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SharingServiceApplication.class, args);
    }

}
