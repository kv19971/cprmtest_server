package com.example.cprmtest.demo;

import com.example.cprmtest.demo.initializer.InitializerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DemoApplication {

	/*
	File format
	stocks - ticker, sd, mean
	customers - hook url
	assets - customer url,stock url,quantity,trade type,[option type,option strike price,expiry date yyyy-mm-dd]
	 */

	@Autowired
	InitializerService initializerService;

	@PostConstruct
	private void triggerInitialization() {
		initializerService.initialize();
		initializerService.listenToMarket();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
