import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
    id("org.jetbrains.compose") version "1.7.3"
    id("org.jetbrains.compose.hot-reload") version "1.0.0-alpha03"
}

kotlin {
    jvm {
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

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3 )

        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmTest.dependencies {
            implementation("junit:junit:4.13.2")

            implementation(libs.expekt)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.macos_arm64)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
        }

        /*jsMain.dependencies {
        }

        iosMain.dependencies {
        }*/

    }
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "io.github.theapache64.retrosheetsample.Main_jvmKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "retrosheet"
            packageVersion = "1.0.0"
        }
    }
}

composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}

// Workaround for https://youtrack.jetbrains.com/issue/KT-49124
afterEvaluate {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        versions.webpackCli.version = "4.10.0"
    }
}