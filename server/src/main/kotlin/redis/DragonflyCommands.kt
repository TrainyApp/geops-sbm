package app.trainy.geops.server.redis

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import io.lettuce.core.codec.RedisCodec
import io.lettuce.core.output.CommandOutput
import io.lettuce.core.output.IntegerOutput
import io.lettuce.core.protocol.CommandArgs
import io.lettuce.core.protocol.CommandType
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.future.await
import kotlin.time.Duration

suspend fun <K : Any, V : Any> RedisClient.connectDragonfly(
    uri: RedisURI,
    codec: RedisCodec<K, V>
): DragonflyCommands<K, V> {
    val connection = connectAsync(codec, uri).await()
    val commands = connection.coroutines()

    return DragonflyCommands(connection, commands, codec)
}

class DragonflyCommands<K : Any, V : Any>(
    private val connection: StatefulRedisConnection<K, V>,
    private val delegate: RedisCoroutinesCommands<K, V>,
    private val codec: RedisCodec<K, V>,
) : RedisCoroutinesCommands<K, V> by delegate {
    @get:JvmName("ktIsActive")
    val isOpen get() = connection.isOpen
    suspend fun hsetex(
        key: K,
        expire: Duration,
        values: Map<K, V>
    ): Long {
        val args = CommandArgs(codec).apply {
            addKey(key)
            add(expire.inWholeSeconds)
            values.forEach { (k, v) -> addKey(k); addValue(v) }
        }
        return dispatchSingle(CommandType.HSETEX, IntegerOutput(codec), args)
    }

    private suspend fun <T : Any> dispatchSingle(
        command: CommandType,
        output: CommandOutput<K, V, T>,
        args: CommandArgs<K, V>
    ) = dispatch(command, output, args).single()

    fun close() = connection.close()
}
