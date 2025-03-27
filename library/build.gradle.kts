plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.publish)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
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

    js {
        browser()
        binaries.executable()
    }

    /*

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "retrosheet"
            isStatic = true
        }
    }*/

    sourceSets {
        commonMain.dependencies {
            implementation("de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:2.1.0")
            implementation(libs.ktorfit)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        jvmMain.dependencies {
        }

        jvmTest.dependencies {
            implementation("com.github.theapache64:expekt:1.0.0")
            implementation("org.mockito:mockito-inline:4.5.1")
            implementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
        }

        /*jsMain.dependencies {
        }

        iosMain.dependencies {
        }*/

    }
}
