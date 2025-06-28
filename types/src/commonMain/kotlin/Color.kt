package app.trainy.geops.types

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmInline

/**
 * Representation of a color encoded as HEX.
 */
@Serializable(with = Color.Serializer::class)
@JvmInline
value class Color(val value: Int) {
    object Serializer : KSerializer<Color> {
        private val format = HexFormat {
            number {
                prefix = "#"
            }
        }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
            "app.trainy.geops.types.Color",
            PrimitiveKind.STRING
        )

        override fun serialize(encoder: Encoder, value: Color) =
            encoder.encodeString(value.value.toHexString(format))

        override fun deserialize(decoder: Decoder): Color =
            Color(decoder.decodeString().hexToInt(format))
    }
}
