package app.trainy.geops.server.routes

import app.trainy.geops.server.Config
import app.trainy.geops.server.core.RedisCache
import app.trainy.geops.server.geops.GeopsClient
import io.ktor.http.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.health() {
    get("healthz") {
        val geopsClient: GeopsClient by application.dependencies
        val redisCache: RedisCache by application.dependencies

        if ((geopsClient.isConnected || !Config.RUN_CRAWLER) && redisCache.isActive) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.ServiceUnavailable)
        }
    }
}
