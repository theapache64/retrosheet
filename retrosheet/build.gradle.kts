plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.publish)
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

    /*js {
        browser()
        binaries.executable()
    }

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

        }

        commonTest.dependencies {
            implementation(kotlin("test"))

        }

        jvmMain.dependencies {
            implementation("de.siegmar:fastcsv:2.1.0")
            implementation("com.squareup.retrofit2:retrofit:2.11.0")

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
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
