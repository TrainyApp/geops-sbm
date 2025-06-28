@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package app.trainy.geops.client

import app.trainy.geops.client.PromiseVehiclePositionClient.Companion.builder
import app.trainy.geops.types.VehiclePosition
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.js.Promise


/**
 * Client for the Geops position server API (for JS callers).
 *
 * @see VehiclePositionClient for a coroutines-based API
 * @see builder
 */
@JsName("VehiclePositionClient")
class PromiseVehiclePositionClient(
    executor: CoroutineContext,
    private val client: VehiclePositionClient
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = executor + SupervisorJob()

    /**
     * Returns [Promise] of a list of all current [positions][VehiclePosition].
     */
    fun getVehiclePositions(): Promise<List<VehiclePosition>> = promise { client.getPositions() }

    /**
     * Returns a [Promise] of the [current position][VehiclePosition] of the train with [trainNumber] or `null`.
     */
    fun getVehiclePosition(trainNumber: String): Promise<VehiclePosition?> = promise { client.getPosition(trainNumber) }

    /**
     * Closes all resources of this client.
     */
    fun close() {
        client.close()
        cancel()
    }

    /**
     * Builder for [app.trainy.geops.client.PromiseVehiclePositionClient]
     */
    class Builder internal constructor() {
        private var executor: CoroutineContext? = null
        private var client: VehiclePositionClient? = null

        /**
         * Sets the [CoroutineContext] of the client.
         */
        fun setExecutor(executor: CoroutineContext): Builder = apply { this.executor = executor }

        /**
         * Sets the underlying [VehiclePositionClient].
         */
        fun setClient(client: VehiclePositionClient): Builder = apply { this.client = client }

        /**
         * Builds a new [app.trainy.geops.client.PromiseVehiclePositionClient] with the specified options
         */
        fun build(): PromiseVehiclePositionClient {
            val executor = executor ?: Dispatchers.Default
            val client = client ?: VehiclePositionClient()

            return PromiseVehiclePositionClient(executor, client)
        }

    }

    companion object {
        /**
         * Creates a new builder for [app.trainy.geops.client.PromiseVehiclePositionClient].
         *
         * @see Builder
         */
        fun builder() = Builder()
    }
}
