package com.dem_ams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class DemAmsApplication {

	public static void main(String[] args) {
        SpringApplication.run(DemAmsApplication.class, args);
	}

}
