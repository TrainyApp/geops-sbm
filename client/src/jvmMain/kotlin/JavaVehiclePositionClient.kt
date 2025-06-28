package app.trainy.geops.client

import app.trainy.geops.client.JavaVehiclePositionClient.Companion.builder
import app.trainy.geops.types.VehiclePosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Client for the Geops position server API for Java callers.
 *
 * @see builder
 * @see VehiclePositionClient for a Kotlin Coroutines API
 */
class JavaVehiclePositionClient private constructor(
    private val executor: ExecutorService,
    private val delegate: VehiclePositionClient
) : CoroutineScope {
    override val coroutineContext = executor.asCoroutineDispatcher() + SupervisorJob()

    /**
     * Returns a [CompletionStage] of a list of all current [positions][VehiclePosition].
     */
    fun getPositionsAsync(): CompletionStage<List<VehiclePosition>> = future { delegate.getPositions() }

    /**
     * Returns a list of all current [positions][VehiclePosition].
     */
    fun getPositions(): List<VehiclePosition> = getPositionsAsync().toCompletableFuture().join()

    /**
     * Returns a [CompletionStage] of the [current position][VehiclePosition] of the train with [trainNumber] or `null`.
     */
    fun getPositionAsync(trainNumber: String): CompletionStage<VehiclePosition?> =
        future { delegate.getPosition(trainNumber) }

    /**
     * Returns the [current position][VehiclePosition] of the train with [trainNumber] or `null`.
     */
    fun getPosition(trainNumber: String): VehiclePosition? = getPositionAsync(trainNumber).toCompletableFuture().join()

    /**
     * Closes all resources used by this client.
     */
    fun close() {
        delegate.close()
        cancel()
        executor.shutdown()
    }

    /**
     * Builder for [app.trainy.geops.client.JavaVehiclePositionClient].
     *
     * @see app.trainy.geops.client.JavaVehiclePositionClient.builder
     */
    class Builder internal constructor() {
        private var executor: ExecutorService? = null
        private var client: VehiclePositionClient? = null

        /**
         * Sets the [executor] used to dispatch requests.
         *
         * Defaults to [Executors.newCachedThreadPool]
         */
        fun setExecutor(executor: ExecutorService): Builder = apply { this.executor = executor }

        /**
         * Sets the underlying [VehiclePositionClient].
         */
        fun setClient(client: VehiclePositionClient): Builder = apply { this.client = client }

        /**
         * Returns a new [app.trainy.geops.client.JavaVehiclePositionClient] with the specified options.
         */
        fun build(): JavaVehiclePositionClient {
            val executor = executor ?: Executors.newCachedThreadPool()
            val client = client ?: VehiclePositionClient()

            return JavaVehiclePositionClient(executor, client)
        }

    }

    companion object {
        /**
         * Creates a new builder.
         *
         * @see Builder
         */
        @JvmStatic
        fun builder() = Builder()
    }
}
