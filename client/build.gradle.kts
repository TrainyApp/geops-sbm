@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    `multiplatform-module`
}

kotlin {
    applyDefaultHierarchyTemplate {
        common {
            group("web") {
                withWasmJs()
                withJs()
            }
        }
    }

    jvm {
        testRuns.all {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.types)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.websockets)
                implementation(libs.ktor.client.resources)
                implementation(libs.ktor.serialization.kotlinx.protobuf)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.ktor.client.mock)
                implementation(libs.kotlinx.coroutines.test)
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.client.java)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.cronet)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }

        named("webMain") {
            dependencies {
                implementation(libs.ktor.client.js)
                implementation(kotlin("test"))
            }
        }

        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        appleMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

        mingwMain {
            dependencies {
                implementation(libs.ktor.client.winhttp)
            }
        }

        if (!HostManager.hostIsMingw) {
            linuxMain {
                dependencies {
                    implementation(libs.ktor.client.curl)
                }
            }
        }
    }
}
