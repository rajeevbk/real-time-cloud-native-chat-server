package com.rajeevbk.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.SessionBuilderConfigurer;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;


/**
 * Provides advanced, code-based configuration for the Cassandra connection.
 * This approach is an alternative to using application.properties and is useful
 * for more complex setups. It directly addresses the connection and configuration
 * errors by explicitly setting the local datacenter and credentials.
 */
@Configuration
@EnableCassandraRepositories(basePackages = "com.rajeevbk.messaging.repository") // Specify the package of your Cassandra repositories
public class CassandraConfiguration extends AbstractCassandraConfiguration {

    // IMPORTANT: Make sure this matches the keyspace you want to use.
    public static final String KEYSPACE = "chat_server";

    // This must match the CASSANDRA_DC value in your docker-compose.yml
    public static final String DATACENTER = "datacenter1";

    // The contact point for the Cassandra container.
    public static final String CONTACT_POINTS = "127.0.0.1";

    // The port exposed in your docker-compose.yml
    public static final int PORT = 9042;

    @Value("${spring.data.cassandra.username}")
    private String username;

    @Value("${spring.data.cassandra.password}")
    private String password;


    /**
     * THIS IS THE FIX: Override getEntityBasePackages() to tell Spring where
     * to find your @Table annotated entities.
     * @return An array containing the package name of your models.
     */
    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"com.rajeevbk.messaging.model"};
    }

    /**
     * Specifies the keyspace name for the application to use.
     * @return The name of the keyspace.
     */
    @Override
    protected String getKeyspaceName() {
        return KEYSPACE;
    }

    /**
     * Specifies the contact points for the Cassandra cluster.
     * @return A comma-separated string of hostnames or IP addresses.
     */
    @Override
    protected String getContactPoints() {
        return CONTACT_POINTS;
    }

    /**
     * Specifies the port for the Cassandra driver to connect to.
     * @return The port number.
     */
    @Override
    protected int getPort() {
        return PORT;
    }

    /**
     * CRITICAL: Specifies the local datacenter for the load balancing policy.
     * This is required by the Cassandra driver and fixes the "local DC must be explicitly set" error.
     * @return The name of the local datacenter.
     */
    @Override
    protected String getLocalDataCenter() {
        return DATACENTER;
    }

    /**
     * Use the SessionBuilderConfigurer to provide authentication details.
     * This is the modern replacement for overriding getUsername() and getPassword().
     * @return A configurer that sets the username and password on the session builder.
     */
    @Override
    protected SessionBuilderConfigurer getSessionBuilderConfigurer() {
        return builder -> builder.withAuthCredentials(username, password);
    }

    /**
     * Determines the schema action to take on startup.
     * 'CREATE_IF_NOT_EXISTS' will automatically create tables based on your @Table entities
     * (like our Message class). This is more robust than using startup scripts.
     * @return The schema action.
     */
    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return Collections.singletonList(
                CreateKeyspaceSpecification.createKeyspace(KEYSPACE)
                        .ifNotExists()
                        .with(KeyspaceOption.DURABLE_WRITES, true)
                        .withSimpleReplication(1)
        );
    }
}