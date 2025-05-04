import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.android.application.get().pluginId)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_22
        }
    }

    sourceSets {
        androidMain.dependencies {
            api(projects.shared)
            implementation(libs.activity.compose)
        }
    }
}

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "io.ygdrasil.webgpu"

        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            excludes += "**/**.sha1"
        }

    }
    namespace = "io.ygdrasil.webgpu"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

