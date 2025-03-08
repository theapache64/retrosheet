plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.publish)
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
            implementation("com.squareup.retrofit2:retrofit:2.9.0")

            implementation("com.squareup.moshi:moshi:1.15.2")
            configurations["kapt"]?.dependencies?.add(project.dependencies.create("com.squareup.moshi:moshi-kotlin-codegen:1.15.2"))
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
