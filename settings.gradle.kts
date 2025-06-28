enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "geops-munich-gradle"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://europe-west3-maven.pkg.dev/mik-music/trainy-dependencies")
        google()
    }
}

include(
    ":types",
    ":server",
    ":client"
)
