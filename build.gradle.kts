plugins {
    org.jetbrains.dokka
}

allprojects {
    group = "app.trainy.geops"
    version = "1.1.0"
}

dependencies {
    dokka(projects.types)
    dokka(projects.client)
}
