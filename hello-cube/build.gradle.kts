import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    //id("org.jetbrains.gradle.apple.applePlugin") version "222.4595-0.23.2"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

kotlin {
    js {
        binaries.executable()
        browser()
    }

    jvm {
        // On to make "JavaExec" work, else we got SourceSet with name 'main' not found, see https://youtrack.jetbrains.com/issue/KT-42683
        withJava()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        browser()
    }

    val xcframeworkName = "WgpuApp"
    val xcf = XCFramework(xcframeworkName)

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "WgpuApp"
            isStatic = true
            xcf.add(this)
            binaryOption("bundleId", "io.ygdrasil.wgpu.$xcframeworkName")
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(projects.shared)
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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}


if (Platform.os == Os.MacOs) {
    project.tasks.register<Exec>("runIosSim") {
        val device = "iPhone 15"
        workingDir = project.buildDir
        val linkExecutableTaskName = when (Platform.architecture) {
            Architecture.X86_64 -> "linkReleaseExecutableIosX64"
            Architecture.AARCH64 -> "linkReleaseExecutableIosSimulatorArm64"
        }
        val binTask = project.tasks.named(linkExecutableTaskName)
        dependsOn(binTask)
        commandLine = listOf(
            "xcrun",
            "simctl",
            "spawn",
            "--standalone",
            device
        )

        println("file ${binTask.get().outputs.files.single()}")
        argumentProviders.add {
            binTask.get().outputs.files.files.single()
                .listFiles()?.firstOrNull { it.extension == "kexe" }
                ?.absolutePath
                ?.let { listOf(it) }
                ?.also { println("out $it") }
                ?: error("No kexe found")
        }
    }
}
/*
apple {
	iosApp {
		productName = "HelloCube"
		sceneDelegateClass = "SceneDelegate"
		dependencies {
			implementation(project(":"))
		}
	}
}*/