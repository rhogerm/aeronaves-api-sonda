package com.sonda.aeronaves.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI aeronavesOpenApi() {
        return new OpenAPI().info(new Info()
                .title("API de Gestao de Aeronaves")
                .description("Teste tecnico - Processo Seletivo Sonda VSS")
                .version("1.0.0"));
    }
}
