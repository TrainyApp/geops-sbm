package app.trainy.geops.server.core

import app.trainy.geops.server.geops.Trajectory
import app.trainy.geops.server.types.TimestampSerializer
import app.trainy.geops.types.*
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.LineString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sinh

private data class PropertyKey<T : Any>(val name: String, val serializer: KSerializer<T>)

private inline fun <reified T : Any> PropertyKey(name: String): PropertyKey<T> = PropertyKey(name, serializer<T>())

private const val R = 6378137.0

@Serializable
private data class Line(
    val id: Int,
    val name: String,
    val color: Color,
    @SerialName("text_color")
    val textColor: Color,
    val stroke: Color,
    val tags: List<String>
)

private object Properties {
    val eventTimestamp = PropertyKey("event_timestamp", TimestampSerializer)

    val trainNumber = PropertyKey<Int>("train_number")
    val trainId = PropertyKey<String>("train_id")
    val transmittingVehicle = PropertyKey<String>("transmitting_vehicle")
    val vehicleNumber = PropertyKey<String>("vehicle_number")

    val line = PropertyKey<Line>("line")
    val hasRealtime = PropertyKey<Boolean>("has_realtime")
    val hasJourney = PropertyKey<Boolean>("has_journey")
    val rawCoordinates = PropertyKey<List<Double>>("raw_coordinates")

    val json = Json {
        isLenient = true
    }
}

private operator fun <T : Any> Feature.get(key: PropertyKey<T>): T = getOrNull(key) ?: error("Property $key not found")
private fun <T : Any> Feature.getOrNull(key: PropertyKey<T>): T? {
    val element = properties[key.name] ?: return null
    return Properties.json.decodeFromJsonElement(key.serializer.nullable, element)
}

fun Trajectory.toVehiclePosition() = content.toVehiclePosition()

private fun Feature.toVehiclePosition(): VehiclePosition? {
    if (!this[Properties.hasJourney]) return null
    val position = toPosition() ?: toEstimatedPosition() ?: return null

    return VehiclePosition(
        toStatus(),
        this[Properties.eventTimestamp],
        toVehicle(),
        toJourney(),
        position
    )
}

private fun Feature.toEstimatedPosition(): Coordinate? {
    val (x, y) = (geometry as? LineString)?.coordinates?.firstOrNull() ?: return null

    val lon = x / R * (180.0 / PI)
    val lat = Math.toDegrees(atan(sinh(y / R)))

    return Coordinate(lat, lon)
}

private fun Feature.toStatus() =
    if (this[Properties.hasRealtime]) VehiclePosition.Status.REALTIME else VehiclePosition.Status.PREDICTED

private fun Feature.toVehicle(): Vehicle = Vehicle(
    getOrNull(Properties.transmittingVehicle),
    this[Properties.trainId],
    getOrNull(Properties.vehicleNumber)
)

private fun Feature.toJourney(): Journey {
    val line = this[Properties.line]
    return Journey(
        this[Properties.trainNumber],
        line.id,
        line.name,
        line.toColor()
    )
}

private fun Line.toColor() = LineColor(color, textColor, stroke)

private fun Feature.toPosition(): Coordinate? {
    val (lon, lat) = getOrNull(Properties.rawCoordinates) ?: return null
    return Coordinate(lat, lon)
}
