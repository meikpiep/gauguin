plugins {
    java
    id("java-test-fixtures")
    id("org.jetbrains.kotlin.jvm")
    jacoco
    `jvm-test-suite`
    kotlin("plugin.serialization") version "1.9.21"
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

    implementation(libs.bundles.koin)

    api(libs.bundles.logging)

    testImplementation(libs.kotlin.coroutines.debug)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.koin.test)
    testImplementation(libs.test.mockk)

    testImplementation(testFixtures(project(":gauguin-core")))
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
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
                implementation(testFixtures(project()))

                implementation.bundle(libs.bundles.logging)
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

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
}
