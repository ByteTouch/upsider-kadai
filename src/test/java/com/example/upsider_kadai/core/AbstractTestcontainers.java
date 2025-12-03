package com.example.upsider_kadai.core;

import org.flywaydb.core.Flyway;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractTestcontainers {

    protected static final PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16.3-alpine"));

    static {
        postgres.start();
        migrate();
    }

    static void migrate() {
        var flyway = Flyway.configure().dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()).load();
        flyway.migrate();
    }
}