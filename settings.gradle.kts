@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("gradle/scripts")
}

plugins {
    id("de.fayard.refreshVersions").version("0.60.5")
}

dependencyResolutionManagement {
    include("http4k-core")
}
