package com.social.backend.config;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ActuatorConfig {
  
  @Bean
  @Profile("dev")
  public HttpTraceRepository httpTraceRepository() {
    return new InMemoryHttpTraceRepository();
  }
  
}
