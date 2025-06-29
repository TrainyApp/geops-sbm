package app.trainy.geops.server.routes

import io.ktor.server.http.content.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Route.openAPI() {
    staticResources("/openapi", "openapi")
    swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
}
