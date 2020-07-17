package com.social.backend.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.social.backend.repository")
@EntityScan("com.social.backend.model")
public class DatabaseConfig {
}
