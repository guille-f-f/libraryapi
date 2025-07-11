package com.egg.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

// Access to documentation: URL/swagger-ui/index.html
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST")
                        .version("1.0")
                        .description("API documentation whit Springdoc OpenAPI")
                        .contact(new Contact()
                                .name("Enterprise")
                                .email("enterprise@gmail.com")));
    }

}
