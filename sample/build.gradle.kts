plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kapt)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
        withJava()
    }

/*    js {
        browser()
        binaries.executable()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "Retrosheet Sample"
            isStatic = true
        }
    }*/

    sourceSets {
        commonMain.dependencies {


        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmTest.dependencies {
            implementation("junit:junit:4.13.2")
        }

        jvmMain.dependencies {
            implementation(project(":retrosheet"))

            // Retrofit : A type-safe HTTP client for Android and Java.
            implementation("com.squareup.retrofit2:retrofit:2.11.0")
            implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
            implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")

            // Kotlinx Coroutines Core : Coroutines support libraries for Kotlin
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

            // OkHttp Logging Interceptor : Square’s meticulous HTTP client for Java and Kotlin.
            implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
        }

        /*jsMain.dependencies {
        }

        iosMain.dependencies {
        }*/

    }
}
