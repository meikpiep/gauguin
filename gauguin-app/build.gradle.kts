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
        resourceConfigurations += setOf("en-rUS", "de-rDE")
    }

    applicationVariants.all {
        this.resValue("string", "versionName", this.versionName)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.txt",
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

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

kotlin {
    jvmToolchain(11)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {

    api(project(":gauguin-core"))
    implementation(libs.koin.android)

    implementation(libs.android.material)

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.transition)
    implementation(libs.androidx.window)

    implementation(libs.thirdparty.konfetti)
    implementation(libs.thirdparty.ferriswheel)
    implementation(libs.thirdparty.navigationdrawer)
}

sonarqube {
    properties {
        property("sonar.androidLint.reportPaths", "$projectDir/build/reports/lint-results-debug.xml")
    }
}
