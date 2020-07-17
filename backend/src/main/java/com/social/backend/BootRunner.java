package com.social.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class BootRunner {
    public static void main(String[] args) {
        SpringApplication.run(BootRunner.class, args);
    }
}
