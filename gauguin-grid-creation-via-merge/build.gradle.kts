plugins {
    java
    id("java-test-fixtures")
    id("org.jetbrains.kotlin.jvm")
    jacoco
    `jvm-test-suite`
    kotlin("plugin.serialization") version "2.0.20"
    id("com.google.devtools.ksp")
}

// Used by Koin
sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
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
    api(libs.androidx.annotation)
    implementation(libs.kotlin.coroutines.core)
    api(libs.kotlin.serialization)
    implementation(project(":gauguin-core"))

    implementation(libs.bundles.koin)

    api(libs.bundles.logging)

    testImplementation(libs.logging.logback.kotlin)
    testImplementation(libs.kotlin.coroutines.debug)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.koin.test)
    testImplementation(libs.test.mockk)

    testImplementation(testFixtures(project(":gauguin-core")))
    testImplementation(project(":gauguin-core"))
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())
                implementation(project(":gauguin-core"))
                implementation(testFixtures(project(":gauguin-core")))

                implementation.bundle(libs.bundles.logging)
                implementation(libs.logging.logback.kotlin)

                implementation.bundle(libs.bundles.kotest)

                implementation(libs.kotlin.coroutines.debug)
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

// tasks.withType<Test> {
//    minHeapSize = "512m"
//    maxHeapSize = "20g"
// }

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
}
