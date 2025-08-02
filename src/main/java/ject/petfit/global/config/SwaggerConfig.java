package ject.petfit.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class SwaggerConfig {
    public static final String JWT_SECURITY_SCHEME = "JWT Token";

    @Value("${spring.backend}")
    private String backendDomain;

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .scheme("bearer")
                .bearerFormat("JWT");
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Token");

        Info info = new Info()
                .title("펫핏 API 명세서")
                .version("v1")
                .description("펫핏 API 명세서입니다 <br> " +
                        "사용할 petId에 해당하는 슬롯 초기화(post /api/slots/{petId})를 반드시 실행하고 나머지 API를 진행해주세요");

        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                .addSecurityItem(securityRequirement)
                .servers(List.of(new io.swagger.v3.oas.models.servers.Server().url(backendDomain)));
    }
}

