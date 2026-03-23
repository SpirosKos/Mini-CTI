package com.mini.cti.core;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/*
  The practical effect is that Swagger UI shows an "Authorize" button where you paste your JWT token.
 */
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {


    /*
        Provides the metadata that appears in Swagger UI's header section —
        purely informational, no functional impact on the API itself
     */
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Mini-CTI")
                        .version("1.0.0")
                        .description("""
                                REST API for managing Cyber Threat Intelligence informations.
                                Provides endpoints to obtain information's about  IP's and a 
                                daily update of latest CVE's from CISA KEV.
                                 
                                 Authentication is done via JWT Bearer tokens.
                                """)
                        .contact(new Contact()
                                .name("Mini CTI")
                                .email("cti@gmail.com"))
                        .license(new License()
                                .name("CC0 1.0 Universal")
                                .url("https://creativecommons.org/publicdomain/zero/1.0")));
    }


    /*
        Automatically injects 401 and 403 responses into every secured operation's
        Swagger documentation, so we don't repeat @ApiResponse annotations everywhere.
    */
    @Bean
    public OperationCustomizer globalSecurityResponses() {
        return (operation, handlerMethod) -> {
            boolean isSecured = handlerMethod.hasMethodAnnotation(SecurityRequirement.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(SecurityRequirement.class);

            if(isSecured) {
                operation.getResponses()
                        .addApiResponse("401", new ApiResponse().description("Unauthorized - JWT token is missing or invalid"))
                        .addApiResponse("403", new ApiResponse().description("Forbidden - You don't have permission to access this resource."));
            }
            return operation;
        };
    }
}
