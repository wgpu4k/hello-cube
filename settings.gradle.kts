rootProject.name = "wgpu4k-hello-cube"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        //wgpu4k snapshot & preview repository
        maven("https://gitlab.com/api/v4/projects/25805863/packages/maven")

    }
}


include("shared")
include("hello-cube")
include("android")