import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kapt)
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

            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25")

            // Retrofit : A type-safe HTTP client for Android and Java.
            implementation("com.squareup.retrofit2:retrofit:2.9.0")
            implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

            // Converter: Moshi : A Retrofit Converter which uses Moshi for serialization.
            implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

            // Kotlinx Coroutines Core : Coroutines support libraries for Kotlin
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

            // Moshi : Moshi
            implementation("com.squareup.moshi:moshi:1.15.2")
            configurations["kapt"]?.dependencies?.add(project.dependencies.create("com.squareup.moshi:moshi-kotlin-codegen:1.15.2"))



            // OkHttp Logging Interceptor : Square’s meticulous HTTP client for Java and Kotlin.
            implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
        }

        /*jsMain.dependencies {
        }

        iosMain.dependencies {
        }*/

    }
}
