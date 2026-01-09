package com.flowcommerce.core.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Flow Commerce API")
                        .description("라이브 커머스 플랫폼 API 문서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("sgh")
                                .email("sgh@github.com")));
    }
}
