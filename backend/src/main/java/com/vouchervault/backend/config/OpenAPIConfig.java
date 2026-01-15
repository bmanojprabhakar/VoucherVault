package com.vouchervault.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI voucherVaultOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("VoucherVault API")
                        .description("Backend API for VoucherVault - A secure coupon management system")
                        .version("v1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
