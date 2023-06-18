plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdkVersion = "android-33"
    buildToolsVersion = "33.0.0"

    defaultConfig {
        applicationId = "com.holokenmod"
        minSdk = 24
        targetSdk = 33
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.txt"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
    lint {
        disable += "ExpiredTargetSdkVersion"
    }
    namespace = "com.holokenmod"
}

repositories {
    google()
    jcenter()
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

kotlin {
    jvmToolchain(8)
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    api(project(":holoken-core"))

    implementation("io.insert-koin:koin-android:3.4.0")

    implementation("com.google.android.material:material:1.9.0")

    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.fragment:fragment-ktx:1.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.window:window:1.0.0")
    implementation("androidx.window:window-java:1.0.0")
    implementation("androidx.preference:preference:1.2.0")
    implementation("androidx.transition:transition:1.4.1")
    implementation("androidx.preference:preference-ktx:1.2.0")

    implementation("nl.dionsegijn:konfetti-xml:2.0.2")
    implementation("ru.github.igla:ferriswheel:1.2")

    implementation("androidx.core:core-ktx:1.10.1")
}
