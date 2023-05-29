buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
    }
}
plugins {
    id("com.android.application").version("8.0.2") apply false
    id("com.android.library").version("8.0.2") apply false
    id("org.jetbrains.kotlin.android").version("1.8.20") apply false
    id("org.jetbrains.kotlin.jvm").version("1.8.20") apply false
    id("org.sonarqube").version("4.0.0.2929")
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
}

sonarqube {
    properties {
        property("sonar.projectKey", "meikpiep_holokenmod")
        property("sonar.organization", "meikpiep")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
