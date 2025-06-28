@file:JsExport

package app.trainy.geops.types

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Representation of a journey.
 *
 * @property trainNumber the train number
 * @property line the line number
 * @property lineName the name of the line displayed to customers
 * @property color the color information for the lines logo
 */
@Serializable
data class Journey(
    val trainNumber: Int,
    val line: Int,
    val lineName: String,
    val color: LineColor
)

/**
 * Representation of line's icon's colors.
 *
 * @property color the background color
 * @property textColor the text color
 * @property stroke the stroke color
 */
@Serializable
data class LineColor(
    val color: Color,
    val textColor: Color,
    val stroke: Color
)
