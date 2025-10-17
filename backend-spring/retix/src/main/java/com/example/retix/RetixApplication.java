package com.example.retix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RetixApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetixApplication.class, args);
	}

}
