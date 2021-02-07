plugins {
  java
  jacoco
  checkstyle
  id("org.springframework.boot").version("2.2.0.RELEASE")
}

repositories {
  mavenCentral()
}

sourceSets {
  create("integrationTest") {
    java {
    }
  }
}

val integrationTestImplementation: Configuration by configurations.getting {
  extendsFrom(configurations.implementation.get())
}

configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dependencies {

  // CDI
  implementation("org.springframework:spring-context:5.2.0.RELEASE")
  implementation("org.springframework.boot:spring-boot-autoconfigure:2.2.0.RELEASE")

  // Common
  implementation("com.google.guava:guava:29.0-jre")
  implementation("org.apache.commons:commons-collections4:4.4")
  implementation("org.apache.commons:commons-lang3:3.9")
  implementation("org.mapstruct:mapstruct:1.3.1.Final")
  annotationProcessor("org.mapstruct:mapstruct-processor:1.3.1.Final")
  implementation("org.projectlombok:lombok:1.18.12")
  annotationProcessor("org.projectlombok:lombok:1.18.12")
  implementation("org.yaml:snakeyaml:1.25")

  // Data
  implementation("com.h2database:h2:1.4.197")
  implementation("com.zaxxer:HikariCP:3.4.1")
  implementation("org.flywaydb:flyway-core:6.0.8")
  implementation("org.hibernate:hibernate-core:5.4.10.Final")
  implementation("org.postgresql:postgresql:42.2.5")
  implementation("org.springframework.data:spring-data-jpa:2.2.0.RELEASE")

  // Logging
  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("org.slf4j:jul-to-slf4j:1.7.30")
  implementation("org.slf4j:slf4j-api:1.7.30")

  // Security
  implementation("org.springframework.security:spring-security-config:5.2.0.RELEASE")
  implementation("org.springframework.security:spring-security-crypto:5.2.0.RELEASE")
  implementation("org.springframework.security:spring-security-web:5.2.0.RELEASE")

  // Validation
  implementation("org.glassfish:javax.el:3.0.0")
  implementation("org.hibernate.validator:hibernate-validator:6.1.5.Final")

  // Web
  implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0")
  implementation("org.apache.tomcat.embed:tomcat-embed-core:9.0.37")
  implementation("org.springframework:spring-webmvc:5.2.0.RELEASE")


  // Test: Asserting
  testImplementation("org.assertj:assertj-core:3.16.1")
  testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
  testImplementation("org.skyscreamer:jsonassert:1.5.0")

  // Test: CDI
  testImplementation("org.springframework:spring-test:5.2.0.RELEASE")

  // Test: Mocking
  testImplementation("org.mockito:mockito-inline:3.4.0")
  testImplementation("org.mockito:mockito-junit-jupiter:3.4.0")

  // Test: Web
  testImplementation("io.rest-assured:rest-assured:4.3.0")
  testImplementation("io.rest-assured:spring-mock-mvc:4.3.0")


  // Integration Test: Asserting
  integrationTestImplementation("junit:junit:4.12")
  integrationTestImplementation("org.assertj:assertj-core:3.16.1")
  integrationTestImplementation("org.skyscreamer:jsonassert:1.5.0")

  // Integration Test: Startup
  integrationTestImplementation("org.testcontainers:postgresql:1.15.0")

  // Integration Test: Web
  integrationTestImplementation("io.rest-assured:rest-assured:4.3.0")

}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

checkstyle {
  toolVersion = "8.32"
}

tasks {
  test {
    useJUnitPlatform()
  }

  wrapper {
    gradleVersion = "5.6.4"
    distributionType = Wrapper.DistributionType.ALL
  }

  task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath

    useJUnit()
  }
}
