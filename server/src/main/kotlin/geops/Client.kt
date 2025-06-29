package app.trainy.geops.server.geops

import app.trainy.geops.server.Config
import app.trainy.geops.server.core.retry.newRetry
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val json = Json {
    ignoreUnknownKeys = true
}

private val geopsWs = Url("wss://api.geops.io/realtime-ws/v1/")
private val pingInterval = 10.seconds

private val LOG = KotlinLogging.logger { }

class GeopsClient {
    private val retry = newRetry()
    private val _events = MutableSharedFlow<GeopsMessage>(
        extraBufferCapacity = Channel.UNLIMITED
    )
    val events = _events.asSharedFlow()

    @OptIn(DelicateCoroutinesApi::class)
    val isConnected get() = session?.isActive == true && session?.incoming?.isClosedForReceive != true
    private var session: DefaultWebSocketSession? = null

    private val safeSession: DefaultWebSocketSession
        get() = session ?: throw IllegalStateException("Client not connected")

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }

        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(json)
        }
    }

    private fun handleEvent(event: GeopsMessage) {
        _events.tryEmit(event)
    }

    suspend fun sendCommand(command: Command) {
        val value = command.formatCommand()
        LOG.debug { "Sending command: $value" }
        safeSession.send(value)
    }

    suspend fun subscribe() {
        sendCommand(BufferCommand(100.milliseconds, 100))
        sendCommand(
            BBoxCommand(
                1230173, 6057470, 1347123, 6201936,
                10,
                "sbm",
            )
        )
    }

    suspend fun connect() = connect(isRetry = false)

    private suspend fun connect(isRetry: Boolean = false) {
        if (isRetry) {
            if (retry.hasNext) {
                retry.retry()
            } else {
                throw IllegalStateException("Max retries exceeded")
            }
        }
        session?.close()
        val currentSession = client.webSocketSession {
            url {
                takeFrom(geopsWs)
                parameters.append("key", Config.GEOPS_KEY)
            }
        }
        LOG.info { "Connected to websocket" }
        retry.reset()
        session = currentSession

        currentSession.launch(CoroutineName("GeopsClient-Pinger")) {
            while (isActive) {
                sendCommand(PingCommand)
                delay(pingInterval)
            }
        }

        subscribe()

        for (message in currentSession.incoming) {
            LOG.trace { "Received frame: $message" }
            val event = client.plugin(WebSockets).contentConverter!!.deserialize<GeopsMessage>(message)
            LOG.debug { "Received event: $event" }
            _events.emit(event)
            handleEvent(event)
        }
        LOG.info { "Lost connection to websocket" }
        connect(isRetry = true)
    }

    suspend inline fun <reified T : GeopsMessage> waitFor(crossinline predicate: (T) -> Boolean = { true }) = events
        .filterIsInstance<T>()
        .filter { predicate(it) }
        .take(1)
        .single()

    inline fun <reified T : GeopsMessage> on(
        scope: CoroutineScope,
        crossinline handler: suspend CoroutineScope.(T) -> Unit
    ) = events
        .filterIsInstance<T>()
        .onEach { element -> scope.launch { handler(this, element) } }
        .launchIn(scope)

    suspend fun close() {
        session?.close()
        client.close()
    }
}
