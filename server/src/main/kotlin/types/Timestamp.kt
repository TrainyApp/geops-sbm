package app.trainy.geops.server.types

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Instant

typealias Timestamp = @Serializable(with = TimestampSerializer::class) Instant

object TimestampSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "app.trainy.geops.server.types.Timestamp",
        PrimitiveKind.LONG
    )

    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeLong(value.toEpochMilliseconds())

    override fun deserialize(decoder: Decoder): Instant = Instant.fromEpochMilliseconds(decoder.decodeLong())
}
