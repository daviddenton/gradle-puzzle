import groovy.namespace.QName
import groovy.util.Node
import java.net.URI

description = "Dependency-lite Server as a Function in pure Kotlin"

plugins {
    kotlin("jvm")
    `java-library`
    signing
    `maven-publish`
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

repositories {
    mavenCentral()
}

val enableSigning = project.findProperty("sign") == "true"

if (enableSigning) { // when added it expects signing keys to be configured
    apply(plugin = "signing")
    signing {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

publishing {
    repositories {
        maven {
            name = "http4k"
            url = URI("s3://http4k-maven")

            val ltsPublishingUser: String? by project
            val ltsPublishingPassword: String? by project

            credentials(AwsCredentials::class.java) {
                accessKey = ltsPublishingUser
                secretKey = ltsPublishingPassword
            }
        }
    }

    publications {
        val archivesBaseName = tasks.named<Jar>("jar").get().archiveBaseName.get()
        create<MavenPublication>("mavenJava") {
            artifactId = archivesBaseName
            pom.withXml {
                asNode().appendNode("name", archivesBaseName)
                asNode().appendNode("description", description)
                asNode().appendNode("url", "https://http4k.org")
                asNode().appendNode("developers")
                    .appendNode("developer").appendNode("name", "Ivan Sanchez").parent()
                    .appendNode("email", "ivan@http4k.org")
                    .parent().parent()
                    .appendNode("developer").appendNode("name", "David Denton").parent()
                    .appendNode("email", "david@http4k.org")
                asNode().appendNode("scm")
                    .appendNode("url", "https://github.com/http4k/http4k").parent()
                    .appendNode("connection", "scm:git:git@github.com:http4k/http4k.git").parent()
                    .appendNode("developerConnection", "scm:git:git@github.com:http4k/http4k.git")
                asNode().appendNode("licenses").appendNode("license")
                    .appendNode("name", "Apache License, Version 2.0").parent()
                    .appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.html")
            }
            from(components["java"])

            // replace all runtime dependencies with provided
            pom.withXml {
                asNode()
                    .childrenCalled("dependencies")
                    .flatMap { it.childrenCalled("dependency") }
                    .flatMap { it.childrenCalled("scope") }
                    .forEach { if (it.text() == "runtime") it.setValue("provided") }
            }
        }
    }
}

fun Node.childrenCalled(wanted: String) = children()
    .filterIsInstance<Node>()
    .filter {
        val name = it.name()
        (name is QName) && name.localPart == wanted
    }
