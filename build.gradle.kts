import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(22))
	}
}

repositories {
    mavenCentral()
	// Snapshot repository
	maven {
		url = uri("https://gitlab.com/api/v4/projects/25805863/packages/maven")
	}
	// Use by rococoa
	maven {
		url = uri("http://repo.maven.cyberduck.io.s3.amazonaws.com/releases")
		isAllowInsecureProtocol = true
	}
}

group = "io.ygdrasil"
version = "0.0.0-SNAPSHOT"


kotlin {
	js {
		binaries.executable()
		browser()
	}
	jvm {
		// On to make "JavaExec" work, else we got SourceSet with name 'main' not found, see https://youtrack.jetbrains.com/issue/KT-42683
		withJava()
	}

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

enum class Os {
	Linux,
	Windows,
	MacOs
}

enum class Architecture {
	X86_64,
	AARCH64
}

object Platform {
	val os: Os
		get() = System.getProperty("os.name").let { name ->
			when {
				arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } -> Os.Linux
				arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } -> Os.MacOs
				arrayOf("Windows").any { name.startsWith(it) } -> Os.Windows
				else -> error("Unrecognized or unsupported operating system.")
			}
		}

	val architecture: Architecture
		get() = System.getProperty("os.arch").let { architecture ->
			when (architecture) {
				"aarch64" -> Architecture.AARCH64
				"x86_64", "amd64" -> Architecture.X86_64
				else -> error("Unrecognized or unsupported architecture $architecture.")
			}
		}

}

tasks.register<JavaExec>("runApp") {
	mainClass = "MainKt"
	if (Platform.os == Os.MacOs) {
		jvmArgs(
			"-XstartOnFirstThread",
			"--add-opens=java.base/java.lang=ALL-UNNAMED",
			"--enable-native-access=ALL-UNNAMED"
		)
	} else {
		jvmArgs(
			"--add-opens=java.base/java.lang=ALL-UNNAMED",
			"--enable-native-access=ALL-UNNAMED"
		)
	}
	classpath = sourceSets["main"].runtimeClasspath
}
