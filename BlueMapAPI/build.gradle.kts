import java.io.IOException
import java.util.concurrent.TimeoutException

plugins {
    java
    `java-library`
    `maven-publish`
    id("com.diffplug.spotless") version "6.1.2"
}

fun String.runCommand(): String = ProcessBuilder(split("\\s(?=(?:[^'\"`]*(['\"`])[^'\"`]*\\1)*[^'\"`]*$)".toRegex()))
    .directory(projectDir)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .start()
    .apply {
        if (!waitFor(10, TimeUnit.SECONDS)) {
            throw TimeoutException("Failed to execute command: '" + this@runCommand + "'")
        }
    }
    .run {
        val error = errorStream.bufferedReader().readText().trim()
        if (error.isNotEmpty()) {
            throw IOException(error)
        }
        inputStream.bufferedReader().readText().trim()
    }

var gitHash = "dev"

group = "de.bluecolored.bluemap.api"
version = "dev"

println("Version: $version")

val javaTarget = 8
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)

    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api ("com.flowpowered:flow-math:1.0.3")
    api ("com.google.code.gson:gson:2.8.0")

    compileOnly ("org.jetbrains:annotations:23.0.0")
}

spotless {
    java {
        target ("src/*/java/**/*.java")

        licenseHeaderFile("LICENSE_HEADER")
        indentWithSpaces()
        trimTrailingWhitespace()
    }
}

tasks.withType(JavaCompile::class).configureEach {
    options.apply {
        encoding = "utf-8"
    }
}

tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

tasks.javadoc {
    options {
        (this as? StandardJavadocDocletOptions)?.apply {
            links(
                "https://docs.oracle.com/javase/8/docs/api/"
            )
        }
    }
}

tasks.processResources {
    from("src/main/resources") {
        include("de/bluecolored/bluemap/api/version.json")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        expand ( mapOf(
            "version" to project.version,
            "gitHash" to gitHash
        ))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}
