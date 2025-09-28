plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.upwork.stock"
version = "0.0.1-SNAPSHOT"
description = "Event Logger Service"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    implementation("org.springframework.boot:spring-boot-starter-json")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
