package com.rezervation.TravelRezervation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TravelRezervationApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelRezervationApplication.class, args);
	}

}
