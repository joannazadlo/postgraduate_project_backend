package io.github.joannazadlo.recipedash.config.properties;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server()
                .url("http://localhost:8080")
                .description("Development");

        Contact contact = new Contact()
                .name("Joanna")
                .email("joanna.m.zadlo@gmail.com");

        Info info = new Info()
                .title("RecipeDash API")
                .version("1.0")
                .description("This API exposes endpoints to manage recipes, users, opinions, " +
                        "user preferences, user ingredients and to search recipes " +
                        "from external sources (MealDB, Tasty)")
                .termsOfService("http://example.com/terms/")
                .contact(contact)
                .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0"));

        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(List.of(server))
                .addSecurityItem(securityRequirement)
                .components(new Components().addSecuritySchemes("bearerAuth", bearerAuth));
    }
}
