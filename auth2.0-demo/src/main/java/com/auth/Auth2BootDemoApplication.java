package com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
//@EnableLogFilter
public class Auth2BootDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(Auth2BootDemoApplication.class, args);
	}
}
