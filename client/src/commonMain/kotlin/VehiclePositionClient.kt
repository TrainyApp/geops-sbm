package app.trainy.geops.client

import app.trainy.geops.types.Route
import app.trainy.geops.types.TrainyInternal
import app.trainy.geops.types.VehiclePosition
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.resources.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.protobuf.*
import kotlin.js.JsName

internal expect val defaultHttpClientEngineFactory: HttpClientEngineFactory<*>

/**
 * @suppress
 */
@TrainyInternal
fun HttpClientConfig<*>.configure() {
    install(ContentNegotiation) {
        protobuf()
    }

    expectSuccess = true

    install(Resources)
}

/**
 * Client for the Geops position server API.
 *
 * @param httpClientEngineFactory the [HttpClientEngineFactory] to use to create a client
 */
@JsName("CoroutinesVehiclePositionClient")
class VehiclePositionClient(httpClientEngineFactory: HttpClientEngineFactory<*> = defaultHttpClientEngineFactory) :
    AutoCloseable {
    private val client = HttpClient(httpClientEngineFactory) {
        configure()
    }

    /**
     * Returns a list of all current [positions][VehiclePosition].
     */
    suspend fun getPositions() = client.get(Route.VehiclePositions()).body<List<VehiclePosition>>()

    /**
     * Returns the [current position][VehiclePosition] of the train with [trainNumber] or `null`.
     */
    suspend fun getPosition(trainNumber: String): VehiclePosition? {
        val response = client.get(Route.VehiclePositions.ByTrain(trainNumber)) {
            expectSuccess = false
        }
        if (response.status == HttpStatusCode.NotFound) return null
        return response.body<VehiclePosition>()
    }

    /**
     * Closes all resources of this client.
     */
    override fun close() {
        client.close()
    }
}
