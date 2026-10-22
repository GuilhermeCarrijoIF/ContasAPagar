package br.com.anna.contaspagarreceber.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI contasPagarReceberOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Contas a Pagar e a Receber")
                        .version("1.0")
                        .description("API REST monolítica baseada no sistema apresentado no livro Delphi 4 - Contas a Pagar e a Receber."));
    }
}
