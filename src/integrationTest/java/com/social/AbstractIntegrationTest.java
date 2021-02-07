package com.social;

import java.io.File;

import org.junit.Rule;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

public abstract class AbstractIntegrationTest {

  @Rule
  public Network network = Network.newNetwork();

  @Rule
  public PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:9.6-alpine")
          .withNetwork(network)
          .withNetworkAliases("postgres")
          .withExposedPorts(5432)
          .withDatabaseName("social-test")
          .withUsername("username")
          .withPassword("password");

  @Rule
  public GenericContainer<?> appContainer =
      new GenericContainer<>(new ImageFromDockerfile()
          .withFileFromFile(".", new File("./")))
          .withNetwork(network)
          .withExposedPorts(8080)
          .withEnv("SPRING_PROFILES_ACTIVE", "test")
          .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://postgres:5432/social-test")
          .withEnv("SPRING_DATASOURCE_USERNAME", "username")
          .withEnv("SPRING_DATASOURCE_PASSWORD", "password")
          .dependsOn(postgresContainer);

}
