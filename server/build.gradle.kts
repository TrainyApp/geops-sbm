@file:Suppress("UnstableApiUsage")

plugins {
    `jvm-module`
    application
    alias(libs.plugins.kotlin.plugin.serialization)
    `jvm-test-suite`
}

dependencies {
    implementation(projects.types)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.di)
    implementation(libs.ktor.client.java)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.resources)
    implementation(libs.ktor.client.websockets)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.serialization.kotlinx.protobuf)

    implementation(libs.spatialk.geojson)

    implementation(libs.envconf)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
    implementation(libs.lettuce.core)

    implementation(libs.jansi)
    implementation(libs.logback)
    implementation(libs.logging)

    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(projects.client)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xdont-warn-on-error-suppression")
        optIn.add("io.lettuce.core.ExperimentalLettuceCoroutinesApi")
    }
}

application {
    mainClass = "app.trainy.geops.server.LauncherKt"
}

testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter()
        }
    }
}
