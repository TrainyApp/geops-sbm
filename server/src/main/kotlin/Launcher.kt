package app.trainy.geops.server

import ch.qos.logback.classic.Logger
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory

suspend fun main() {
    coroutineScope {
        initializeLogging()
        Server.start()

        awaitCancellation()
    }
}

private fun initializeLogging() {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as? Logger
    if (rootLogger == null) {
        LoggerFactory.getLogger("GeopsMunich")
            .warn("Could not set log level due to different logging engine being used")
        return
    }
    rootLogger.level = Config.LOG_LEVEL
}
