rootProject.name = "retrosheet"
include(":library")
include(":sample")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        // jitpack
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}