package com.craftmaster.lds.fgc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAspectJAutoProxy
@SpringBootApplication
public class FgcApplication {

  public static void main(String[] args) {
    SpringApplication.run(FgcApplication.class, args);
  }
}

//notes
// -Dspring.session.jdbc.initialize-schema=always