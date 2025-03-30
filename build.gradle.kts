plugins {
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktorfit) apply false
    alias(libs.plugins.androidApp) apply false
}
