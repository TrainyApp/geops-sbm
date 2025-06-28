package app.trainy.geops.types

import io.ktor.resources.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Resource("/{version}")
data class Route(val version: Version = Version.V1) {
    @Serializable
    enum class Version {
        @SerialName("v1")
        V1
    }

    @Resource("vehicle-positions")
    data class VehiclePositions(val parent: Route = Route()) {
        @Resource("{trainId}")
        data class ByTrain(val trainId: String, val parent: VehiclePositions = VehiclePositions())
    }
}
