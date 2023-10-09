plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdkVersion = "android-34"
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "org.piepmeyer.gauguin"
        minSdk = 24
        targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.txt"
            )
            resValue("bool", "debuggable", "false")
        }

        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            resValue("bool", "debuggable", "true")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    lint {
        disable += "ExpiredTargetSdkVersion"
    }
    namespace = "org.piepmeyer.gauguin"
}

repositories {
    google()
    jcenter()
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

kotlin {
    jvmToolchain(8)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {

    api(project(":gauguin-core"))
    implementation("io.insert-koin:koin-android")

    implementation("com.google.android.material:material:1.9.0")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.transition:transition:1.4.1")
    implementation("androidx.window:window:1.1.0")

    implementation("nl.dionsegijn:konfetti-xml:2.0.3")
    implementation("ru.github.igla:ferriswheel:1.2")
}
