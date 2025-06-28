@file:JsExport
package app.trainy.geops.types

import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.time.Instant

/**
 * Representation of a vehicles current position.
 *
 * @property status the current [position status][Status]
 * @property lastUpdate the [Instant] of the positions last update
 * @property vehicle information about the [Vehicle] submitting this position
 * @property journey the [Journey] this position is for
 * @property position the [Coordinate] of the current position
 */
@Suppress("SERIALIZER_NOT_FOUND")
@Serializable
data class VehiclePosition(
    val status: Status,
    val lastUpdate: Instant,
    val vehicle: Vehicle,
    val journey: Journey,
    val position: Coordinate,
) {
    @Serializable
    enum class Status {
        REALTIME,
        PREDICTED,
    }
}
