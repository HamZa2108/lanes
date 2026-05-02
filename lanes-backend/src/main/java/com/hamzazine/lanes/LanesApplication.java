package com.hamzazine.lanes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LanesApplication {
	public static void main(String[] args) {
		SpringApplication.run(LanesApplication.class, args);
	}
}