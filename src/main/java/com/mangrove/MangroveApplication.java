package com.mangrove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MangroveApplication {

    public static void main(String[] args) {
        SpringApplication.run(MangroveApplication.class, args);
    }
}
