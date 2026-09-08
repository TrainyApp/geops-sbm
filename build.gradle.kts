plugins {
    org.jetbrains.dokka
}

allprojects {
    group = "app.trainy.geops"
    version = "1.2.0"
}

dependencies {
    dokka(projects.types)
    dokka(projects.client)
}
