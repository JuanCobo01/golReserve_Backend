package com.golReserve.gol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GolApplication {

	public static void main(String[] args) {
		SpringApplication.run(GolApplication.class, args);
	}

}
