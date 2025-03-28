plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.publish)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
}

kotlin {
    jvmToolchain(17)

    jvm {
        withJava()
    }

    js {
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
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktorfit)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.serialization.csv)
        }

        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":sample"))
            implementation(libs.expekt)
            implementation(libs.mockito.inline)
            implementation(libs.mockito.kotlin)
        }
    }
}


tasks.named("sourcesJar") {
    dependsOn("kspCommonMainKotlinMetadata")
}