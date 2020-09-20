plugins {
  java
  jacoco
  checkstyle
  id("org.springframework.boot") version "2.2.0.RELEASE"
  id("io.spring.dependency-management") version "1.0.9.RELEASE"
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
  testImplementation("io.rest-assured:rest-assured:4.2.0")
  testImplementation("io.rest-assured:spring-mock-mvc:4.2.0")

  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-web")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
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
