package com.example.upsider_kadai.core;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import org.springframework.boot.restdocs.test.autoconfigure.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;

@TestConfiguration(proxyBeanMethods = false)
public class TestConfig extends AbstractTestcontainers {

    @Bean
    DynamicPropertyRegistrar dynamicPropertyRegistrar() {
        return props -> {
            props.add("spring.datasource.url", postgres::getJdbcUrl);
            props.add("spring.datasource.username", postgres::getUsername);
            props.add("spring.datasource.password", postgres::getPassword);
        };
    }

    @Bean
    RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer() {
        return config -> config.operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint());
    }
}
