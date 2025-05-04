plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

fun PluginDependency.asLibrary(): Any = "$pluginId:$pluginId.gradle.plugin:$version"
fun Provider<PluginDependency>.asLibrary(): Provider<Any> = map { it.asLibrary() }

dependencies {
    implementation(libs.plugins.kotlin.multiplatform.asLibrary())
    implementation(libs.plugins.android.library.asLibrary())
}