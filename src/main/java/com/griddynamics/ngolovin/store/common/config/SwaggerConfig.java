package com.griddynamics.ngolovin.store.common.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String API_ANT_PATTERN = "/api/**";
    private static final String JWT_AUTHORIZATION = "JWT";

    @Bean
    public Docket api() {
        //noinspection Guava
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.or(PathSelectors.ant(API_ANT_PATTERN), PathSelectors.ant("/auth/**")))
                .build()
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Collections.singletonList(jwtApiKey()));
    }

    private ApiKey jwtApiKey() {
        return new ApiKey(JWT_AUTHORIZATION, "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(jwtSecurityReference()))
                .forPaths(PathSelectors.ant(API_ANT_PATTERN))
                .build();
    }

    private SecurityReference jwtSecurityReference() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");

        return new SecurityReference(JWT_AUTHORIZATION, new AuthorizationScope[] { authorizationScope });
    }
}
