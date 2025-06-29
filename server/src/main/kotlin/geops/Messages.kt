package app.trainy.geops.server.geops

import app.trainy.geops.server.types.Timestamp
import io.github.dellisd.spatialk.geojson.Feature
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
@JsonClassDiscriminator("source")
sealed interface GeopsMessage {
    val timestamp: Timestamp

    @SerialName("client_reference")
    val clientReference: String
    val content: Any
}

@Serializable
@SerialName("websocket")
data class WebsocketMessage(
    override val timestamp: Timestamp,
    @SerialName("client_reference")
    override val clientReference: String,
    override val content: JsonElement
) : GeopsMessage {
    val isPong: Boolean get() = content.toString() == "PONG"
    fun decode() = Json.decodeFromJsonElement<Content>(content)

    @Serializable
    data class Content(
        val status: String? = null,
        val id: String? = null,
        val info: String? = null,
    )
}

@SerialName("buffer")
@Serializable
data class Buffer(
    override val timestamp: Timestamp,
    @SerialName("client_reference")
    override val clientReference: String,
    override val content: List<GeopsMessage?>
) : GeopsMessage

@SerialName("trajectory")
@Serializable
data class Trajectory(
    override val timestamp: Timestamp,
    @SerialName("client_reference")
    override val clientReference: String,
    override val content: Feature
) : GeopsMessage

@SerialName("deleted_vehicles")
@Serializable
data class DeletedVehicles(
    override val timestamp: Timestamp,
    @SerialName("client_reference")
    override val clientReference: String,
    override val content: String
) : GeopsMessage
