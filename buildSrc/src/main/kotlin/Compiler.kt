package app.trainy.geops.build

import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

val androidSdkInt = 36
val jvmTarget = JvmTarget.JVM_21
val javaVersion = JavaVersion.toVersion(jvmTarget.target)

fun KotlinJvmCompilerOptions.applyOptions() {
    (this as KotlinCommonCompilerOptions).applyOptions()
    jvmTarget = app.trainy.geops.build.jvmTarget
}

fun KotlinCommonCompilerOptions.applyOptions() {
    optIn.addAll(
        "kotlin.time.ExperimentalTime",
        "kotlinx.serialization.ExperimentalSerializationApi",
        "app.trainy.geops.types.TrainyInternal",
        "kotlin.js.ExperimentalJsExport"
    )
}
