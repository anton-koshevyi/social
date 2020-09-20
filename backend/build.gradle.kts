plugins {
  java
  jacoco
  checkstyle
  id("org.springframework.boot").version("2.2.0.RELEASE")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.google.guava:guava:29.0-jre")
  implementation("com.h2database:h2:1.4.198")
  implementation("org.apache.commons:commons-lang3:3.9")
  implementation("org.flywaydb:flyway-core:6.0.8")
  implementation("org.mapstruct:mapstruct:1.3.1.Final")
  annotationProcessor("org.mapstruct:mapstruct-processor:1.3.1.Final")
  implementation("org.postgresql:postgresql:42.2.5")
  implementation("org.projectlombok:lombok:1.18.12")
  annotationProcessor("org.projectlombok:lombok:1.18.12")

  implementation("org.springframework.boot:spring-boot-starter-actuator:2.2.0.RELEASE")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.2.0.RELEASE")
  implementation("org.springframework.boot:spring-boot-starter-security:2.2.0.RELEASE")
  implementation("org.springframework.boot:spring-boot-starter-web:2.2.0.RELEASE")


  // Test: Asserting
  testImplementation("org.assertj:assertj-core:3.16.1")
  testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
  testImplementation("org.skyscreamer:jsonassert:1.5.0")

  // Test: CDI
  testImplementation("org.springframework:spring-test:5.2.0.RELEASE")
  testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:2.2.0.RELEASE")

  // Test: Mocking
  testImplementation("org.mockito:mockito-inline:3.4.0")
  testImplementation("org.mockito:mockito-junit-jupiter:3.4.0")

  // Test: Web
  testImplementation("io.rest-assured:rest-assured:4.3.0")
  testImplementation("io.rest-assured:spring-mock-mvc:4.3.0")

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
}
