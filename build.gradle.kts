import java.net.URI

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.11.2")
        classpath("org.jacoco:org.jacoco.core:0.8.11")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roborazzi) apply false
}

sonarqube {
    properties {
        property("sonar.projectKey", "org.piepmeyer.gauguin")
        property("sonar.organization", "meikpiep")
        property("sonar.verbose", "true")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

tasks.sonar {
    onlyIf("There is no property 'buildserver'") {
        project.hasProperty("buildserver")
    }
    dependsOn(":gauguin-app:lint")
}

sonarqube {
    properties {
        property("sonar.coverage.jacoco.xmlReportPaths", "$projectDir/build/reports/jacoco.xml")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { url = URI("https://jitpack.io") }
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
