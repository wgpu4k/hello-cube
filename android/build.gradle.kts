import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget = JvmTarget.JVM_22
        }
    }

    sourceSets {
        androidMain.dependencies {
            api(projects.shared)
            implementation("androidx.core:core-ktx:1.7.0")
            implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
            implementation("androidx.activity:activity-compose:1.3.1")
        }
    }
}

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "io.ygdrasil.wgpu"

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
    namespace = "io.ygdrasil.wgpu"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

