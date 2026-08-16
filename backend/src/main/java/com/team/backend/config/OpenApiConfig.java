package com.team.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Task Management API")
        .version("v1")
        .description("""
                    Personal Task Management API with Dynamic Authorization (Role & Permission).

                    **Quick Testing Guide:**
                    1. Call the /api/auth/login API to get an access token.
                    2. Click the **Authorize** 🔓 in the top right corner and paste your token (no need to include the word "Bearer").")
                    3. From now on, all requests will automatically include the token.
                    """)
        .contact(new Contact()
          .name("Thanh thanh")
          .url("https://api.thanh-lab.site")))
      .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
      .components(new Components()
        .addSecuritySchemes("bearerAuth", new SecurityScheme()
          .name("bearerAuth")
          .type(SecurityScheme.Type.HTTP)
          .scheme("bearer")
          .bearerFormat("JWT")));
  }

  @Bean
  public GroupedOpenApi authApi() {
    return GroupedOpenApi.builder()
      .group("0. Auth")
      .pathsToMatch("/auth/**")
      .build();
  }

  @Bean
  public GroupedOpenApi userApi() {
    return GroupedOpenApi.builder()
      .group("1. User APIs")
      .pathsToMatch("/tasks/**", "/categories/**")
      .build();
  }

  @Bean
  public GroupedOpenApi adminApi() {
    return GroupedOpenApi.builder()
      .group("2. Admin APIs")
      .pathsToMatch("/admin/**", "/roles/**", "/permissions/**")
      .build();
  }
}
