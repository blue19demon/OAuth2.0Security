package com.funtl.oauth2.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderTest {

	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode("secret"));
	}

}
