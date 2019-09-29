package com.craftmaster.lds.fgc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
      .authorizeRequests()
      .antMatchers("/api/**").authenticated()
      .antMatchers("/game-ws").authenticated()
      .antMatchers("/register/**").anonymous()
      .anyRequest().permitAll()
      .and().httpBasic();
//				.antMatchers("/", "/home").permitAll()
//				.anyRequest().authenticated()
//				.and()
//				.formLogin()
//				.loginPage("/login")
//				.permitAll()
//				.and()
//				.logout()
//				.permitAll();
  }
}

