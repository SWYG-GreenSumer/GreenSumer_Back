package org.swyg.greensumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GreenSumerBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenSumerBackApplication.class, args);
    }

}
