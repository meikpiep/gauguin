buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.1")
    }
}
plugins {
    id("com.android.application").version("8.0.1") apply false
    id("com.android.library").version("8.0.1") apply false
    id("org.jetbrains.kotlin.android").version("1.8.0") apply false
    id("org.sonarqube").version("4.0.0.2929")
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
}
