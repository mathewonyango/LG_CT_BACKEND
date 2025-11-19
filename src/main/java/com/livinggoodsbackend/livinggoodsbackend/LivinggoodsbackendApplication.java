package com.livinggoodsbackend.livinggoodsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LivinggoodsbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivinggoodsbackendApplication.class, args);
	}

}
