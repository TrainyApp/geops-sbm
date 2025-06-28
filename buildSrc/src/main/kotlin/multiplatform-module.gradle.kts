@file:OptIn(ExperimentalWasmDsl::class)

import app.trainy.geops.build.androidSdkInt
import app.trainy.geops.build.applyOptions
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.dokka.gradle.workers.ProcessIsolation
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    org.jetbrains.kotlin.multiplatform
    com.android.library
    com.vanniktech.maven.publish
    org.jetbrains.dokka
}

kotlin {
    jvm {
        compilerOptions {
            applyOptions()
        }
    }

    androidTarget {
        publishLibraryVariants("release")
    }

    js {
        browser()
        nodejs()
    }

    // https://youtrack.jetbrains.com/issue/KT-62385/
//    wasmJs {
//        browser()
//    }

//    wasmWasi {
//        nodejs()
//    }

    if (!HostManager.hostIsMingw) {
        linuxX64()
        linuxArm64()
    }

    macosX64()
    macosArm64()

    mingwX64()

    iosArm64()
    iosSimulatorArm64()
    iosX64()

    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()

    watchosArm64()
    watchosSimulatorArm64()
    watchosX64()

    compilerOptions {
        applyOptions()
    }
}

android {
    namespace = "app.trainy.geops.${project.name}"
    compileSdk = androidSdkInt
}

dokka {
    // Dokka runs out of memory with the default maxHeapSize when ProcessIsolation is used
    (dokkaGeneratorIsolation.get() as? ProcessIsolation)?.maxHeapSize = "1g"

    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory = project.projectDir
            remoteUrl("https://github.com/trainyapp/geops-sbm/blob/main")
            remoteLineSuffix = "#L"
        }

        externalDocumentationLinks {
            register("ktor") {
                url = uri("https://api.ktor.io/")
            }
        }
    }
}

mavenPublishing {
    configure(
        KotlinMultiplatform(
            sourcesJar = true,
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            androidVariantsToPublish = listOf("release")
        )
    )
    coordinates("com.trainyapp.geops", project.name)
    publishToMavenCentral(automaticRelease = true)

    signAllPublications()

    pom {
        name = "geops-sbm"
        description = "Client for the Trainy Geops service"
        url = "https://github.com/trainyapp/geops-sbm"

        organization {
            name = "Trainy"
            url = "https://github.com/geops"
        }

        developers {
            developer {
                name = "Michael Rittmeister"
            }
        }

        issueManagement {
            system = "GitHub"
            url = "https://github.com/trainyapp/geops-sbm/issues"
        }

        licenses {
            license {
                name = "MIT"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        scm {
            connection = "scm:git:ssh://github.com/trainyapp/geops-sbm.git"
            developerConnection = "scm:git:ssh://git@github.com:rainyapp/geops-sbm.git"
            url = "https://github.com/trainyapp/geops-sbm"
        }
    }
}
