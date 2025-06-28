package app.trainy.geops.server.routes

import app.trainy.geops.server.core.RedisCache
import app.trainy.geops.types.Route.VehiclePositions
import io.ktor.server.plugins.*
import io.ktor.server.plugins.di.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.vehiclePositions() {
    val cache: RedisCache by application.dependencies

    get<VehiclePositions> {
        call.respond(cache.getPositions())
    }

    get<VehiclePositions.ByTrain> { (trainNumber) ->
        val positions = cache.getPosition(trainNumber) ?: throw NotFoundException()

        call.respond(positions)
    }
}
