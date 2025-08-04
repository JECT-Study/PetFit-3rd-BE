package ject.petfit.global.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    public static final String JWT_SECURITY_SCHEME = "JWT Token";

    @Value("${app.swagger-server}")
    private String swaggerServer;
    @Value("${app.swagger-description}")
    private String swaggerDescription;
    @Value("${app.swagger-url}")
    private String swaggerUrl;

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
                .title(swaggerServer + " API 명세서")
                .version("v1")
                .description("펫핏 API 명세서입니다");

        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                .addSecurityItem(securityRequirement)
                .servers(List.of(new Server().url(swaggerUrl).description(swaggerServer)));
    }
}
