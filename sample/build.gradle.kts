plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
}

kotlin {
    jvm {
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

            implementation(libs.expekt)
        }

        jvmMain.dependencies {
            implementation(project(":library"))
            implementation(libs.ktorfit)
            implementation("io.ktor:ktor-client-content-negotiation:3.1.1")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.1")

            // Kotlinx Coroutines Core : Coroutines support libraries for Kotlin
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")


            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
        }

        /*jsMain.dependencies {
        }

        iosMain.dependencies {
        }*/

    }
}
