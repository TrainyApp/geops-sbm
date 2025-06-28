package app.trainy.geops.server

import app.trainy.geops.server.core.PositionHandler
import app.trainy.geops.server.core.RedisCache
import app.trainy.geops.server.geops.GeopsClient
import app.trainy.geops.server.routes.vehiclePositions
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.protobuf.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.di.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.lettuce.core.RedisURI
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

object Server {
    suspend fun start() = coroutineScope {
        val server = embeddedServer(Netty, host = Config.HOST, port = Config.PORT) {
            module()
        }

        server.startSuspend()
    }
}

internal suspend fun Application.module(redisUrl: RedisURI = Config.REDIS_URL) {
    val positionHandler: PositionHandler by dependencies
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
    }

    install(ContentNegotiation) {
        json()
        protobuf()
    }

    install(Resources)

    dependencies {
        provide { GeopsClient() } cleanup { runBlocking { it.close() } }
        provide { RedisCache.connect(redisUrl) } cleanup { it.close() }
        provide {
            val redis = resolve<RedisCache>()
            val geopsClient = resolve<GeopsClient>()

            PositionHandler(redis, geopsClient)
        } cleanup { it.close() }
    }

    if (Config.RUN_CRAWLER) {
        positionHandler.connect()
    }

    routing {
        vehiclePositions()
    }
}
