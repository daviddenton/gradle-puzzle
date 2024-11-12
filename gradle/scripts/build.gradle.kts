plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    api(gradleApi())
}
