plugins {
    java
    id("org.jetbrains.kotlin.jvm")
    jacoco
    `jvm-test-suite`
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
    api("androidx.annotation:annotation:1.6.0")

    implementation("io.insert-koin:koin-core:3.4.3")

    api("io.github.oshai:kotlin-logging-jvm:5.0.2")
    testApi("io.github.oshais:kotlin-logging-jvm:5.0.2")
    api("org.slf4j:slf4j-simple:2.0.7")

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

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())

                implementation("io.github.oshai:kotlin-logging-jvm:5.0.2")
                implementation("org.slf4j:slf4j-simple:2.0.7")

                implementation("io.kotest:kotest-runner-junit5:5.6.2")
                implementation("io.kotest:kotest-assertions-core:5.6.2")
                implementation("io.kotest:kotest-framework-datatest:5.6.2")
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
