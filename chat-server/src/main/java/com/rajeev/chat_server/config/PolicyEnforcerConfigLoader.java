package com.rajeev.chat_server.config;

import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PolicyEnforcerConfigLoader {
    public static PolicyEnforcerConfig loadConfig(String propertiesFilePath) {
        Properties properties = new Properties();
        try (InputStream input = PolicyEnforcerConfigLoader.class.getResourceAsStream(propertiesFilePath)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + propertiesFilePath);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load properties file", ex);
        }

        PolicyEnforcerConfig config = new PolicyEnforcerConfig();
        config.setRealm(properties.getProperty("realm"));
        config.setAuthServerUrl(properties.getProperty("kc-auth-server-url"));
        config.setResource(properties.getProperty("resource"));

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("secret", properties.getProperty("credentials.secret"));
        config.setCredentials(credentials);

        config.setHttpMethodAsScope(Boolean.parseBoolean(properties.getProperty("http-method-as-scope")));

        // Load paths
        List<PolicyEnforcerConfig.PathConfig> paths = new ArrayList<>();
        for (int i = 0; ; i++) {
            String path = properties.getProperty("paths[" + i + "].path");
            if (path == null) break;
            String enforcementMode = properties.getProperty("paths[" + i + "].enforcement-mode");
            PolicyEnforcerConfig.PathConfig pathConfig = new PolicyEnforcerConfig.PathConfig();
            pathConfig.setPath(path);
            pathConfig.setEnforcementMode(PolicyEnforcerConfig.EnforcementMode.valueOf(enforcementMode));
            paths.add(pathConfig);
        }
        config.setPaths(paths);

        return config;
    }
}
