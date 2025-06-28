@file:JsExport

package app.trainy.geops.types

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Representation of a coordinate in [WGS 84](https://en.wikipedia.org/wiki/World_Geodetic_System).
 *
 * @property latitude the latitude
 * @property longitude the longitude
 */
@Serializable
data class Coordinate(val latitude: Double, val longitude: Double)
