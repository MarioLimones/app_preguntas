package com.app.quiz.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Preguntas Quiz API")
                        .version("1.0.0")
                        .description("API REST para gestión de preguntas de tipo Verdadero/Falso, "
                                + "Selección Única y Selección Múltiple. "
                                + "Incluye gestión de usuarios, categorías y resultados de tests.")
                        .contact(new Contact()
                                .name("App Preguntas")
                                .email("info@preguntas.app"))
                        .license(new License()
                                .name("MIT")));
    }
}



