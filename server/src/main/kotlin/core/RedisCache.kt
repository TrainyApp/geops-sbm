package app.trainy.geops.server.core

import app.trainy.geops.server.Config
import app.trainy.geops.server.redis.DragonflyCommands
import app.trainy.geops.server.redis.connectDragonfly
import app.trainy.geops.types.VehiclePosition
import io.github.oshai.kotlinlogging.KotlinLogging
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.codec.ByteArrayCodec
import io.lettuce.core.codec.RedisCodec
import io.lettuce.core.codec.StringCodec
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.time.Duration

private const val indexKey = "vehicle_positions"

private val LOG = KotlinLogging.logger { }

private val codec = RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE)

class RedisCache private constructor(private val commands: DragonflyCommands<String, ByteArray>) {
    private val format = ProtoBuf

    suspend fun insertPositions(position: List<VehiclePosition>, expire: Duration) {
        val results = commands.hsetex(
            indexKey,
            expire,
            position.associate { it.journey.trainNumber.toString() to format.encodeToByteArray(it) }
        )
        LOG.debug { "Inserted position $position into Redis: $results" }
    }

    suspend fun insertPosition(position: VehiclePosition, expire: Duration) {
        val key = position.journey.trainNumber.toString()
        val results = commands.hsetex(
            indexKey,
            expire,
            mapOf(key to format.encodeToByteArray(position))
        )
        LOG.debug { "Inserted position $position into Redis: $results" }
    }

    suspend fun getPositions(): List<VehiclePosition> = commands.hgetall(indexKey)
        .map { it.value.toVehiclePosition() }
        .filterNotNull()
        .toList()

    suspend fun getPosition(id: String): VehiclePosition? {
        val value = commands.hget(indexKey, id) ?: return null

        return value.toVehiclePosition()
    }

    private fun ByteArray.toVehiclePosition(): VehiclePosition? {
        if (size <= 1) return null
        return format.decodeFromByteArray<VehiclePosition>(this)
    }

    fun close() = commands.close()

    companion object {
        suspend fun connect(url: RedisURI): RedisCache {
            val client = RedisClient.create()
            val redis = client.connectDragonfly(url, codec)
            return RedisCache(redis)
        }
    }
}
