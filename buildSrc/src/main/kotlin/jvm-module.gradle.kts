import app.trainy.geops.build.applyOptions
import app.trainy.geops.build.javaVersion

plugins {
    org.jetbrains.kotlin.jvm
}

kotlin {
    compilerOptions {
        applyOptions()
    }
}

java {
    sourceCompatibility = javaVersion
}
