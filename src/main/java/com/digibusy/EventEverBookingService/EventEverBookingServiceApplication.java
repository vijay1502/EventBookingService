package com.digibusy.EventEverBookingService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
//@EnableFeignClients(basePackages = "com.digibusy.EventEverBookingService.Configuration")
public class EventEverBookingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventEverBookingServiceApplication.class, args);
	}

}
