package com.social.hook;

import java.io.File;

import com.zaxxer.hikari.HikariDataSource;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import com.social.test.ContextKeys;
import com.social.test.HooksOrders;
import com.social.test.SharedContext;

public class TestContainersHooks {

  private Network network;
  private JdbcDatabaseContainer<?> postgresContainer;
  private GenericContainer<?> appContainer;

  @Before(order = HooksOrders.TEST_CONTAINERS)
  public void startContainers() {
    network = Network.newNetwork();

    postgresContainer = new PostgreSQLContainer<>("postgres:9.6-alpine")
        .withNetwork(network)
        .withNetworkAliases("postgres")
        .withExposedPorts(5432)
        .withDatabaseName("social-test")
        .withUsername("username")
        .withPassword("password");
    postgresContainer.start();

    appContainer = new GenericContainer<>(new ImageFromDockerfile()
        .withFileFromFile(".", new File("./")))
        .withNetwork(network)
        .withExposedPorts(8080)
        .withEnv("SPRING_PROFILES_ACTIVE", "test")
        .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://postgres:5432/social-test")
        .withEnv("SPRING_DATASOURCE_USERNAME", "username")
        .withEnv("SPRING_DATASOURCE_PASSWORD", "password")
        .dependsOn(postgresContainer);
    appContainer.start();

    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setDriverClassName(postgresContainer.getDriverClassName());
    dataSource.setJdbcUrl(postgresContainer.getJdbcUrl());
    dataSource.setUsername(postgresContainer.getUsername());
    dataSource.setPassword(postgresContainer.getPassword());
    SharedContext.put(ContextKeys.DATASOURCE_POSTGRES, dataSource);

    Integer applicationPort = appContainer.getFirstMappedPort();
    SharedContext.put(ContextKeys.PORT_APPLICATION, applicationPort);
  }

  @After(order = HooksOrders.TEST_CONTAINERS)
  public void stopContainers() {
    appContainer.close();
    postgresContainer.close();
    network.close();
  }

}
