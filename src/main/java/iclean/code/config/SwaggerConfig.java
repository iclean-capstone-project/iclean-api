package iclean.code.config;

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

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customizeOpenAPI() {

        List<Server> servers = new ArrayList<>();
        servers.add(new Server().url("http://localhost:8080"));
        servers.add(new Server().url("https://iclean.azurewebsites.net"));
        final String securitySchemeName = "bearerAuth";

        Contact contact = new Contact();
        contact.setEmail("iclean.service2001@gmail.com");
        contact.setName("iClean");
        contact.setUrl("https://iclean.azurewebsites.net");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("iClean Application API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage iClean application.").termsOfService("https://www.iclean.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .servers(servers)
                .info(info)
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
