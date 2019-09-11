package com.craftmaster.lds.fgc;

import javax.persistence.Entity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class FgcApplication {

	public static void main(String[] args) {
		SpringApplication.run(FgcApplication.class, args);
	}

}

