plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.jooq)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.flyway)
}

group = "com.slimczes"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.jooq)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.kafka)
    implementation(libs.mapstruct)
    compileOnly(libs.lombok)
    runtimeOnly(libs.postgresql)
    jooqGenerator(libs.postgresql)
    annotationProcessor(libs.lombok)
    kapt(libs.mapstruct.processor)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.kafka.test)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.kafka)
    testImplementation(libs.testcontainers.postgresql)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jooq {
    version.set(libs.versions.jooq.get())
    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5434/payments_db"
                    user = "myuser"
                    password = "secret"
                }
                generator.apply {
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        includes = ".*"
                        excludes = ""
                    }
                    target.apply {
                        packageName = "com.slimczes.payments.jooq.generated"
                        directory = "build/generated-src/jooq/main"
                    }
                }
            }
        }
    }
}

flyway {
    url = "jdbc:postgresql://localhost:5434/payments_db"
    user = "myuser"
    password = "secret"
    locations = arrayOf("classpath:db/migration")
    schemas = arrayOf("public")
    baselineOnMigrate = true
    driver = "org.postgresql.Driver"
}

