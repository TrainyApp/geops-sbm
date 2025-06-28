package app.trainy.geops.server

import ch.qos.logback.classic.Level
import dev.schlaubi.envconf.Config
import io.lettuce.core.RedisURI
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

object Config : Config() {
    val HOST by getEnv("0.0.0.0")
    val PORT by getEnv(8080, String::toInt)

    val GEOPS_KEY by this

    val LOG_LEVEL by getEnv(Level.INFO, Level::valueOf)

    val CACHE_LIFETIME by getEnv(5.minutes, Duration::parse)
    val RUN_CRAWLER by getEnv(true, String::toBooleanStrict)
    val REDIS_URL by getEnv(RedisURI.create("redis://localhost:6379/0"), RedisURI::create)
}
