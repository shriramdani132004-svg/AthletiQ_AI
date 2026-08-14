package com.athletiq.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.athletiq.backend.security.jwt.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class AthletiQBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AthletiQBackendApplication.class, args);
	}

}
