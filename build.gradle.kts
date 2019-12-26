plugins {
    `java-library`
    `maven-publish`
    signing
    jacoco
    id("me.champeau.gradle.jmh") version "0.5.0"
    id("com.vanniktech.maven.publish")  version "0.8.0"
}

repositories {
    mavenCentral()
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

//  No compile time dependencies for the main artifact.
dependencies {
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.5.2")

    jmh(group = "org.openjdk.jmh", name = "jmh-core", version = "1.22")
    jmh(group = "org.openjdk.jmh", name = "jmh-generator-annprocess", version = "1.22")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = false
        csv.isEnabled = false
        html.isEnabled = true
        html.destination = file("$buildDir/reports/coverage")
    }
}

// Require 100% test coverage
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "1.0".toBigDecimal()
            }
        }
    }
}

// Bind the check task to running jacoco to keep us honest.
tasks.check {
    dependsOn("jacocoTestCoverageVerification")
}

jmh {
    duplicateClassesStrategy = DuplicatesStrategy.WARN
    iterations = 10
    warmupIterations = 1
    batchSize = 1
    threads = 1
    fork = 1
    operationsPerInvocation = 1
    profilers = listOf("gc")
    jmhVersion = "1.22"
}
