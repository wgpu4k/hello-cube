import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
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
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget = JvmTarget.JVM_22
        }
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
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

    when(Platform.os) {
         Os.MacOs -> when(Platform.architecture) {
             Architecture.X86_64 -> macosArm64("osx")
             Architecture.AARCH64 -> macosX64("osx")
         }
        else -> null // Not supported
    }?.let { nativeTarget ->
        with(nativeTarget) {

            binaries {
                executable {
                    entryPoint = "main"
                }
            }
        }
    }

    sourceSets {

        commonMain {
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

tasks.register<JavaExec>("runJvm") {
    group = "run"
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
