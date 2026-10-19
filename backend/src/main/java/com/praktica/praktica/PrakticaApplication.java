package com.praktica.praktica;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrakticaApplication {
	public static void main(String[] args) {
		SpringApplication.run(PrakticaApplication.class, args);
	}
}
