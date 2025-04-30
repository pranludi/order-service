import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
  java
  id("org.springframework.boot") version "3.4.5"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "com.polarbookshop"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

repositories {
  mavenCentral()
}

extra["springCloudVersion"] = "2024.0.1"
extra["testcontainersVersion"] = "1.21.0"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  //
  implementation("org.springframework.cloud:spring-cloud-starter-config")
  implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")
  //
  runtimeOnly("org.postgresql:r2dbc-postgresql")
  //
  implementation("org.flywaydb:flyway-database-postgresql")
  runtimeOnly("org.postgresql:postgresql")
  implementation("org.springframework:spring-jdbc")
  //
  runtimeOnly("io.micrometer:micrometer-registry-prometheus")
  runtimeOnly("io.opentelemetry.javaagent:opentelemetry-javaagent:2.15.0")
  //
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:postgresql")
  testImplementation("org.testcontainers:r2dbc")
  testImplementation("org.testcontainers:rabbitmq")
  testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
  testImplementation("com.github.dasniko:testcontainers-keycloak:3.7.0")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

springBoot {
  buildInfo()
}

tasks.named<BootBuildImage>("bootBuildImage") {
  imageName.set(project.name)
  environment.set(
    mapOf(
      "BP_JVM_VERSION" to "21.*"
    )
  )
  docker {
    publishRegistry {
      username = project.findProperty("registryUsername") as String?
      password = project.findProperty("registryToken") as String?
      url = project.findProperty("registryUrl") as String?
    }
  }
}
