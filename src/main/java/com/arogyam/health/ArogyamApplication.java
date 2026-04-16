package com.arogyam.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableCaching
@EnableScheduling
@EnableAsync
public class ArogyamApplication {

	public static void main(String[] args) {

		SpringApplication.run(ArogyamApplication.class, args);
	}

}
