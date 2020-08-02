package com.social.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  
  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  
  @Autowired
  public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }
  
  @Bean
  public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf(CsrfConfigurer::disable)
        .authorizeRequests(c -> c
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAuthority(Authority.ADMIN)
            .antMatchers("/auth").anonymous()
            .antMatchers("/logout").authenticated()
            .antMatchers(HttpMethod.POST, "/account").anonymous()
            .antMatchers("/account/**").authenticated()
            .antMatchers(HttpMethod.GET, "/posts", "/posts/{id}").permitAll()
            .antMatchers("/posts", "/posts/{id}").authenticated()
            .antMatchers(HttpMethod.GET, "/posts/{postId}/comments").permitAll()
            .antMatchers("/posts/{postId}/comments/**").authenticated()
            .antMatchers("/chats/**").authenticated()
            .antMatchers(HttpMethod.GET, "/users/**").permitAll()
            .antMatchers("/users/{id}/roles").hasAuthority(Authority.ADMIN)
            .antMatchers("/users/{id}/friends").authenticated()
            .antMatchers("/users/{id}/chats/private").authenticated()
            .anyRequest().permitAll())
        .formLogin(c -> c
            .loginProcessingUrl("/auth")
            .usernameParameter("username")
            .passwordParameter("password")
            .successHandler(new SimpleUrlAuthenticationSuccessHandler("/account")))
        .logout(c -> c
            .logoutUrl("/logout")
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()))
        .exceptionHandling(c -> c
            .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
            .accessDeniedHandler(new AccessDeniedHandlerImpl()));
  }
  
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }
  
  public static final class Authority {
    
    public static final String MODER = "ROLE_MODER";
    public static final String ADMIN = "ROLE_ADMIN";
    
    private Authority() {
    }
    
  }
  
}
