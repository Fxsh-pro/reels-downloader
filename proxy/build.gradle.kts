plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.instagram"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core:9.2.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")


    implementation("org.springframework.boot:spring-boot-starter-jooq:3.2.2")
    implementation("org.jooq:jooq:3.16.5") // Use the latest version of JOOQ
    implementation("org.jooq:jooq-meta:3.16.5")
    implementation("org.jooq:jooq-codegen:3.16.5")
    runtimeOnly("org.postgresql:postgresql:42.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    /**
     * Telegram
     */
    implementation(group = "org.telegram", name = "telegrambots", version = "6.9.0")
    /**
     * Instagram
     */
    implementation("com.github.instagram4j:instagram4j:2.0.7")

    implementation("io.minio:minio:8.5.5") // MinIO SDK
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/kotlin")
    }
}