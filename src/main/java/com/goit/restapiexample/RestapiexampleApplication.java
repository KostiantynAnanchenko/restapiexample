package com.goit.restapiexample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;




@SpringBootApplication
public class RestapiexampleApplication {

	@Value("${server.port}")
	private int port;


	public static void main(String[] args) {
		SpringApplication.run(RestapiexampleApplication.class, args);
	}

}
