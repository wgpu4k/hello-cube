@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
	id(libs.plugins.kotlin.multiplatform.get().pluginId)
}

if (useAndroid) {
	apply(plugin = "android")
}

kotlin {
	js { browser() }

	jvm {
		compilerOptions {
			jvmTarget = JvmTarget.JVM_22
		}
	}

	wasmJs { browser() }

	if (useAndroid) {
		androidTarget {
			compilerOptions {
				jvmTarget = JvmTarget.JVM_22
			}
		}
	}

	iosX64()
	iosArm64()
	iosSimulatorArm64()
	macosX64()
	macosArm64()

	sourceSets {

		 commonMain {
			dependencies {
				api(libs.wgpu4k)
				api(libs.korge.foundation)
				api(libs.coroutines)
			}
		}
	}

	compilerOptions {
		allWarningsAsErrors = true
	}
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(22))
	}
}
