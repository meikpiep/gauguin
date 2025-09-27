pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.9.0")
}

rootProject.name = "gauguin"

include(":gauguin-core")
include(":gauguin-human-solver")
include(":gauguin-grid-creation-via-merge")
include(":gauguin-app")
include(":micro-benchmark")
