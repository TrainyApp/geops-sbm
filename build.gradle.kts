plugins {
    org.jetbrains.dokka
}

allprojects {
    group = "app.trainy.geops"
    version = "1.3.0"
}

dependencies {
    dokka(projects.types)
    dokka(projects.client)
}
