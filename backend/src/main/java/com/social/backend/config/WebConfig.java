package com.social.backend.config;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Bean
  public ErrorAttributes errorAttributes(MessageSource messageSource, Environment env) {
    boolean includeException = env.getProperty(
        "server.error.include-exception", Boolean.class, false);
    return new LocalizedErrorAttributes(messageSource, includeException);
  }

  @Bean
  @Profile("!prod")
  public CommonsRequestLoggingFilter loggingFilter() {
    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
    filter.setIncludeClientInfo(true);
    filter.setIncludePayload(true);
    return filter;
  }

}
