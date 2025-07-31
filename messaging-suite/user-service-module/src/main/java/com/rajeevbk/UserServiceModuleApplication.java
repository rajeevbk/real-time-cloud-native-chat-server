package com.rajeevbk;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
@PropertySources({
        @PropertySource("classpath:application.properties")
})
@EnableScheduling
public class UserServiceModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceModuleApplication.class, args);
    }

    @PostConstruct
    public void logProps() {
       //Run code to Debug (e.g.. loaded DB URL)
    }
}

