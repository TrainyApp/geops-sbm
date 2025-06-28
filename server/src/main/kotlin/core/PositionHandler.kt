package app.trainy.geops.server.core

import app.trainy.geops.server.Config
import app.trainy.geops.server.geops.Buffer
import app.trainy.geops.server.geops.GeopsClient
import app.trainy.geops.server.geops.Trajectory
import app.trainy.geops.server.geops.WebsocketMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private val LOG = KotlinLogging.logger { }

class PositionHandler(private val redisCache: RedisCache, private val client: GeopsClient) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

    init {
        client.on<Trajectory>(this) { handleEvent(it) }
        client.on<Buffer>(this) { handleEvent(it) }
    }

    fun close() {
        coroutineContext.cancel()
    }

    suspend fun connect() {
        println("Connecting to websocket")
        launch { client.connect() }

        client.waitFor<WebsocketMessage> { !it.isPong && it.decode().status == "open" }
        LOG.debug { "WebSocket now ready" }

    }

    suspend fun handleEvent(event: Buffer) {
        val trajectories = event.content
            .asSequence()
            .filterIsInstance<Trajectory>()
            .map { it.toVehiclePosition() }
            .filterNotNull()
            .toList()

        if (trajectories.isEmpty()) return
        LOG.debug { "Handling ${trajectories.size} trajectories" }
        redisCache.insertPositions(trajectories, Config.CACHE_LIFETIME)
    }

    suspend fun handleEvent(event: Trajectory) {
        LOG.debug { "Handling event: $event" }
        val position = event.toVehiclePosition() ?: return
        LOG.debug { "Received position: $position" }

        redisCache.insertPosition(position, Config.CACHE_LIFETIME)
    }
}
