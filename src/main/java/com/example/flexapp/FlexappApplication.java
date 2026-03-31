package com.example.flexapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlexappApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlexappApplication.class, args);
	}

}