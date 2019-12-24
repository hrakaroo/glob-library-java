plugins {
    `java-library`
    `maven-publish`
    signing
    jacoco
    id("me.champeau.gradle.jmh") version "0.5.0"
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

// Create the javadoc and sources artifacts
java {
    withJavadocJar()
    withSourcesJar()
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

//tasks.model {
//    tasks.generatePomFileForMavenJavaPublication {
//        destination = file("$buildDir/generated-pom.xml")
//    }
//}

val sonatypeUsername: String by project
val sonatypePassword: String by project

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.hrakaroo"
            artifactId = "glob"
            version = "1.0.0"
            from(components["java"])
        }

        create<MavenPublication>("mavenJava") {
            pom {
                name.set("Glob Library")
                description.set("Glob matching library")
                url.set("https://github.com/hrakaroo/glob-library-java/")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/hrakaroo/glob-library-java/blob/master/LICENSE.txt")
                    }
                }
                developers {
                    developer {
                        id.set("hrakaroo")
                        name.set("Joshua Gerth")
                        email.set("jgerth@hrakaroo.com")
                    }
                }
                scm {
                    url.set("https://github.com/hrakaroo/glob-library-java/")
                    connection.set("scm:git:git@github.com:hrakaroo/glob-library-java.git")
                    developerConnection.set("scm:git:ssh://git@github.com:hrakaroo/glob-library-java.git")
                }
            }
        }
    }

    repositories {
        maven {
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = sonatypeUsername
                password  = sonatypePassword
            }
        }
    }
}

