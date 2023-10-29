buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2")
    }
}
plugins {
    id("com.android.application").version("8.1.2") apply false
    id("com.android.library").version("8.1.2") apply false
    id("org.jetbrains.kotlin.android").version("1.9.10") apply false
    id("org.jetbrains.kotlin.jvm").version("1.9.10") apply false
    id("org.sonarqube").version("4.4.1.3373")
    id("org.jlleitschuh.gradle.ktlint") version "11.6.0"
}

sonarqube {
    isSkipProject = true
    properties {
        property("sonar.projectKey", "org.piepmeyer.gauguin")
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
