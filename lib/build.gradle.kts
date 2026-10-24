plugins {
    // Provides kotlin-jvm, java-library, maven-publish, ktlint, detekt and jacoco
    id("printscript.common-conventions") version "1.0.4"
}

group = "printscript.grupo1"
version = project.findProperty("version")?.toString()?.takeIf { it != "unspecified" }
    ?: "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Annotations appear in the public types (JobRequest/JobResponse), so they are exposed: api
    api("com.fasterxml.jackson.core:jackson-annotations:2.18.2")

    // The serializer is an internal detail: implementation
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "printscript-jobs"
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Age-Of-PrintScript/printscript-jobs")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
