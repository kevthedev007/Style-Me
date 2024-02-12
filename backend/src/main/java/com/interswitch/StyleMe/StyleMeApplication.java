package com.interswitch.StyleMe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoAuditing
@EnableScheduling
public class StyleMeApplication {

    public static void main(String[] args) {
        SpringApplication.run(StyleMeApplication.class, args);

    }
}
