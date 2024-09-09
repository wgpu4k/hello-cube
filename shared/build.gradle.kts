import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidLibrary)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}


kotlin {
	js { browser() }

	jvm()

	@OptIn(ExperimentalWasmDsl::class)
	wasmJs { browser() }

	androidTarget()

	sourceSets {

		val commonMain by getting {
			dependencies {
				api(libs.wgpu4k)
				api(libs.korge.foundation)
				api(libs.coroutines)
			}
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
