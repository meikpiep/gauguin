plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.benchmark)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "org.piepmeyer.gauguin.benchmark"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        targetSdk = 36

        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.profiling.sampleFrequency"] = "100"
//        testInstrumentationRunnerArguments["androidx.benchmark.profiling.sampleDurationSeconds"] = "5000"
//        testInstrumentationRunnerArguments["androidx.benchmark.profiling.mode"] = "StackSampling"
    }

    testBuildType = "release"
    buildTypes {
        debug {
            // Since isDebuggable can"t be modified by gradle for library modules,
            // it must be done in a manifest - see src/androidTest/AndroidManifest.xml
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "benchmark-proguard-rules.pro",
            )
        }
        release {
            isDefault = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}

dependencies {
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.benchmark.junit4)

    androidTestImplementation(project(":gauguin-core"))
    androidTestImplementation(project(":gauguin-human-solver"))
    androidTestImplementation(project(":gauguin-grid-creation-via-merge"))
    androidTestImplementation(testFixtures(project(":gauguin-core")))
}
