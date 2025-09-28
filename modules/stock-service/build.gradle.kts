plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.upwork.stock"
version = "0.0.1-SNAPSHOT"
description = "Stock Sync Service - Dev case"

dependencies {
    // spring-boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // h2
    runtimeOnly("com.h2database:h2")

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
