
import com.github.triplet.gradle.androidpublisher.ReleaseStatus
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.triplet)
}

val keystoreProperties = Properties()
val keystoreExists = rootProject.file("keystore.properties").exists()

if (keystoreExists) {
    // Create a variable called keystorePropertiesFile, and initialize it to your
    // keystore.properties file, in the rootProject folder.
    val keystorePropertiesFile = rootProject.file("keystore.properties")

    // Load your keystore.properties file into the keystoreProperties object.
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    compileSdkVersion = "android-34"
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "org.piepmeyer.gauguin"
        minSdk = 24
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (keystoreExists) {
        signingConfigs {
            register("release") {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    applicationVariants.all {
        this.resValue("string", "versionName", this.versionName)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    buildTypes {
        release {
            if (keystoreExists) {
                signingConfig = signingConfigs.getByName("release")
            }

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
        disable += listOf("ExpiredTargetSdkVersion", "MissingTranslation")
    }
    namespace = "org.piepmeyer.gauguin"

    androidResources {
        generateLocaleConfig = true
    }
}

play {
    defaultToAppBundles.set(true)
    track.set("production")
    releaseStatus.set(ReleaseStatus.COMPLETED)
    serviceAccountCredentials.set(file("../gauguin-serviceaccount-gradle-play-plugin.json"))
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
    implementation(libs.androidx.window.core)

    implementation(libs.thirdparty.konfetti)
    implementation(libs.thirdparty.ferriswheel)
    implementation(libs.thirdparty.navigationdrawer)
    implementation(libs.thirdparty.balloon)
    implementation(libs.thirdparty.vico)

    implementation(libs.bundles.koin)

    testImplementation(libs.bundles.kotest)
    testImplementation(libs.koin.test)
    testImplementation(libs.test.mockk)
}

sonarqube {
    properties {
        property("sonar.androidLint.reportPaths", "$projectDir/build/reports/lint-results-debug.xml")
    }
}
