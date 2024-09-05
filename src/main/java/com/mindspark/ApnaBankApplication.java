package com.mindspark;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// http://localhost:8090/swagger-ui/index.html#/
@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "The Apna Bank Application",
				description = "Backend REST APIs for Apna Bank",
				version = "v1.0",
				contact = @Contact(
						name = "Akash Nagalwade",
						email = "akashnagalwade444@gmail.com",
						url = "http://github.com/akashnagalwade/Apna-Bank-Application"
				),
				license = @License(
						name = "Mind Spark Technology",
						url = "https://github.com/akashnagalwade/Apna-Bank-Application"

				)
		),
		externalDocs = @ExternalDocumentation(
				description = "The Apna Bank API Documentation",
				url = "https://github.com/akashnagalwade/Apna-Bank-Application"
		)
)
public class ApnaBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApnaBankApplication.class, args);
	}

}
