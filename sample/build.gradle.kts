plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
    id("org.jetbrains.compose") version "1.7.0-alpha03"
}

kotlin {
    jvm {
        withJava()
    }

    js(IR) {
        nodejs {
            testTask {
                useMocha {
                    timeout = "9s"
                }
            }
        }
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }

    /*

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
            implementation(project(":library"))
            implementation(libs.ktorfit)
            implementation("io.ktor:ktor-client-content-negotiation:3.1.1")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmTest.dependencies {
            implementation("junit:junit:4.13.2")

            implementation(libs.expekt)
        }

        jvmMain.dependencies {

        }

        jsMain.dependencies {
            implementation(compose.html.core)
            implementation(compose.runtime)
        }

        /*jsMain.dependencies {
        }

        iosMain.dependencies {
        }*/

    }
}

// Workaround for https://youtrack.jetbrains.com/issue/KT-49124
rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
}
