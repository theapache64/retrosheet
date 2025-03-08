rootProject.name = "retrosheet-library"
include(":retrosheet")
include(":call-adapters")
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