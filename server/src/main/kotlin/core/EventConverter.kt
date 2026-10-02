package app.trainy.geops.server.core

import app.trainy.geops.server.geops.Trajectory
import app.trainy.geops.server.types.Timestamp
import app.trainy.geops.types.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.LineString
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sinh

private const val R = 6378137.0

typealias TrainPosition = Feature<LineString, TrainProperties>

@Serializable
data class Line(
    val id: Int,
    val name: String,
    val color: Color,
    @SerialName("text_color")
    val textColor: Color,
    val stroke: Color,
    val tags: List<String>
)

@Serializable
data class TrainProperties(
    @SerialName("event_timestamp")
    val eventTimestamp: Timestamp,
    @SerialName("train_number")
    val trainNumber: Int? = null,
    @SerialName("train_id")
    val trainId: String,
    @SerialName("transmitting_vehicle")
    val transmittingVehicle: String? = null,
    @SerialName("vehicle_number")
    val vehicleNumber: String? = null,
    @SerialName("line")
    val line: Line,
    @SerialName("has_realtime")
    val hasRealtime: Boolean,
    @SerialName("has_journey")
    val hasJourney: Boolean,
    @SerialName("raw_coordinates")
    val rawCoordinates: List<Double>? = null
)


fun Trajectory.toVehiclePosition() = content.toVehiclePosition()

private fun TrainPosition.toVehiclePosition(): VehiclePosition? {
    if (!properties.hasJourney) return null
    val position = toPosition() ?: toEstimatedPosition() ?: return null

    return VehiclePosition(
        toStatus(),
        properties.eventTimestamp,
        toVehicle(),
        toJourney(),
        position
    )
}

private fun TrainPosition.toEstimatedPosition(): Coordinate? {
    val (x, y) = geometry.coordinates.firstOrNull() ?: return null

    val lon = x / R * (180.0 / PI)
    val lat = Math.toDegrees(atan(sinh(y / R)))

    return Coordinate(lat, lon)
}

private fun TrainPosition.toStatus() =
    if (properties.hasRealtime) VehiclePosition.Status.REALTIME else VehiclePosition.Status.PREDICTED

private fun TrainPosition.toVehicle(): Vehicle = Vehicle(
    properties.transmittingVehicle,
    properties.trainId,
    properties.vehicleNumber
)

private fun TrainPosition.toJourney(): Journey {
    val line = properties.line
    return Journey(
        properties.trainNumber!!,
        line.id,
        line.name,
        line.toColor()
    )
}

private fun Line.toColor() = LineColor(color, textColor, stroke)

private fun TrainPosition.toPosition(): Coordinate? {
    val (lon, lat) = properties.rawCoordinates ?: return null
    return Coordinate(lat, lon)
}
