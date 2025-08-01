package com.rajeevbk;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Messaging Service API", version = "1.0", description = ""))
@SecurityScheme(
        name = "Keycloak"
        , openIdConnectUrl = "${keycloak.openid-connect-url}"
        , scheme = "bearer"
        , type = SecuritySchemeType.OPENIDCONNECT
        , in = SecuritySchemeIn.HEADER
)
public class DirectMessagingModuleApplication {
    public static void main(String[] args) {
        try{
            SpringApplication.run(DirectMessagingModuleApplication.class, args);
        }
        catch (Exception e){
                throw e;
        }
    }
}