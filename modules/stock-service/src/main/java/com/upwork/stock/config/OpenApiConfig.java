package com.upwork.stock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI stockOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Stock Sync Service API")
                                .version("1.0")
                                .description("API to fetch unified inventory and see sync status.")
                                .contact(
                                        new Contact()
                                                .name("Alexander Vivas")
                                                .name("falexvr@gmail.com")
                                )
                );
    }
}
