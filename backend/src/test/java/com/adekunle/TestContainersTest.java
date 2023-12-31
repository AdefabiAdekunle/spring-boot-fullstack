package com.adekunle;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class TestContainersTest extends AbstractTestContainersUnitTest{
//Absract class has been created for it
//    @Container
//    private static final PostgreSQLContainer<?> postgreSQLContainer =
//            new PostgreSQLContainer<>("postgres:latest")
//                    .withDatabaseName("amigoscode-dao-unit-test")
//                    .withUsername("amigoscode")
//                    .withPassword("password");
//
//    @DynamicPropertySource
//    private static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
//        registry.add(
//                "spring.datasource.url",
//                postgreSQLContainer::getJdbcUrl
//        );
//
//        registry.add(
//                "spring.datasource.username",
//                postgreSQLContainer::getUsername
//        );
//
//        registry.add(
//                "spring.datasource.password",
//                postgreSQLContainer::getPassword
//        );
//    }

    @Test
    void canStartPostgresDb() {
        assertThat(postgreSQLContainer.isRunning()).isTrue();
        assertThat(postgreSQLContainer.isCreated()).isTrue();
    }

//Absract class has been created for it
//    @Test
//    void canApplyDbMigrationsWithFlyway() {
//        Flyway flyway = Flyway.configure().dataSource(
//                postgreSQLContainer.getJdbcUrl(),
//                postgreSQLContainer.getUsername(),
//                postgreSQLContainer.getPassword()
//        ).load();
//        flyway.migrate();
//    }
}
