plugins {
    `multiplatform-module`
    alias(libs.plugins.kotlin.plugin.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.ktor.resources)
            }
        }
    }
}
