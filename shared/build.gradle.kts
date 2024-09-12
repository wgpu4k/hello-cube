import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidLibrary)
}

kotlin {
	js { browser() }

	jvm {
		@OptIn(ExperimentalKotlinGradlePluginApi::class)
		compilerOptions {
			jvmTarget = JvmTarget.JVM_22
		}
	}

	@OptIn(ExperimentalWasmDsl::class)
	wasmJs { browser() }

	androidTarget {
		@OptIn(ExperimentalKotlinGradlePluginApi::class)
		compilerOptions {
			jvmTarget = JvmTarget.JVM_22
		}
	}

	sourceSets {

		 commonMain {
			dependencies {
				api(libs.wgpu4k)
				api(libs.korge.foundation)
				api(libs.coroutines)
			}
		}

		androidMain {
			//implementation("io.ygdrasil:wgpu4k-toolkit:")
		}

	}

	@OptIn(ExperimentalKotlinGradlePluginApi::class)
	compilerOptions {
		allWarningsAsErrors = true
	}
}

android {
	namespace = "io.ygdrasil.shared"
	compileSdk = 35

	defaultConfig {
		minSdk = 28
	}

}


java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(22))
	}
}