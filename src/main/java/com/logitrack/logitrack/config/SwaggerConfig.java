package com.logitrack.logitrack.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Swagger/OpenAPI Configuration for LogiTrack API
 * 
 * Swagger UI will be available at: http://localhost:8080/swagger-ui.html
 * OpenAPI JSON will be available at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        try {
            return new OpenAPI()
                    .info(new Info()
                            .title("LogiTrack API")
                            .version("1.0.0")
                            .description("Digital Logistics Supply Chain Management API")
                            .contact(new Contact()
                                    .name("LogiTrack Team")
                                    .email("info@logitrack.com")
                                    .url("https://logitrack.com"))
                            .license(new License()
                                    .name("Apache 2.0")
                                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                    .addServersItem(new Server()
                            .url("http://localhost:8080")
                            .description("Local Development Server"))
                    .addServersItem(new Server()
                            .url("https://api.logitrack.com")
                            .description("Production Server"));
        } catch (Exception e) {
            System.err.println("Error creating OpenAPI configuration: " + e.getMessage());
            e.printStackTrace();
            // Return a minimal OpenAPI configuration as fallback
            return new OpenAPI()
                    .info(new Info()
                            .title("LogiTrack API")
                            .version("1.0.0")
                            .description("Digital Logistics Supply Chain Management API"));
        }
    }
}
