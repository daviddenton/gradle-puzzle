@file:Suppress("UnstableApiUsage")

rootProject.name = "http4k"

pluginManagement {
    includeBuild("gradle/scripts")
}

plugins {
    id("de.fayard.refreshVersions").version("0.60.5")
}

dependencyResolutionManagement {
    include("http4k-core")
}