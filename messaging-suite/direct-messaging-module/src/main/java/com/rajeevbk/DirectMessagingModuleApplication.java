package com.rajeevbk;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "ICP Messaging Service API", version = "1.0", description = ""))
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
            if (isCassandraConnectionException(e)) {
                System.err.println("\n****************************************************************");
                System.err.println("********** CASSANDRA CONNECTION FAILED           **********");
                System.err.println("****************************************************************");
                System.err.println("\nCould not connect to the Cassandra database.");
                System.err.println("Please ensure the Cassandra container is running and accessible.\n");
                System.err.println("  1. Run 'docker-compose up -d' in your terminal.");
                System.err.println("  2. Verify the connection details in 'application.properties'.\n");
                System.err.println("Application will now exit.");
                System.err.println("****************************************************************\n");

                // Exit the application with a non-zero status code to indicate an error
                System.exit(1);
            } else {
                // For any other type of exception, re-throw it to see the default stack trace
                throw e;
            }
        }
    }

    /**
     * Helper method to recursively check the exception chain for specific
     * Cassandra connection error messages or types. This makes the check more robust.
     * * @param throwable The exception to inspect.
     * @return true if a known Cassandra connection exception is found, false otherwise.
     */
    private static boolean isCassandraConnectionException(Throwable throwable) {
        if (throwable == null) {
            return false;
        }

        String message = throwable.getMessage() != null ? throwable.getMessage() : "";

        // Check for specific phrases in the exception message that indicate a connection failure
        if (message.contains("Could not reach any contact point") ||
                message.contains("ConnectionInitException")) {
            return true;
        }

        // Recursively check the cause of the exception
        return isCassandraConnectionException(throwable.getCause());
    }
}