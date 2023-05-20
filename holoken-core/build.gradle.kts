plugins {
    java
    id("org.jetbrains.kotlin.jvm")
    jacoco
}

tasks {
    test {
        useJUnitPlatform()
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
    }
}

dependencies {
    //api "org.apache.commons:commons-lang3:3.12.0"
    api("org.apache.commons:commons-io:1.3.2")
    api("androidx.annotation:annotation:1.6.0")

    api("io.github.microutils:kotlin-logging-jvm:3.0.5")

    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("io.kotest:kotest-framework-datatest:5.6.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}