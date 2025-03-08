plugins {
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.serialization) apply false
}