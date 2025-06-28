@file:JsExport

package app.trainy.geops.types

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Representation of a vehicle on the network.
 *
 * @property uic the [UIC-number](https://en.wikipedia.org/wiki/UIC_identification_marking_for_tractive_stock)
 *               of the transmitting vehicle (only specified if data is provided through GPS)
 * @property trainId the GeOps train id of the current journey
 */
@Serializable
data class Vehicle(val uic: String?, val trainId: String, val vehicleNumber: String?)
