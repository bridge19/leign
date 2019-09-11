package io.github.bridge.leign;

import io.github.bridge.leign.annotation.EnableLeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableLeignClients(basePackages = "io.github.bridge.leign.service")
public class LeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeignApplication.class, args);
    }

}
