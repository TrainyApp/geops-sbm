import app.trainy.geops.client.configure
import app.trainy.geops.server.module
import app.trainy.geops.types.Route
import app.trainy.geops.types.VehiclePosition
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.lettuce.core.RedisURI
import kotlinx.coroutines.delay
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

private val LOG = KotlinLogging.logger { }

@Testcontainers
class GeopsTest {

    @Container
    private val redis = GenericContainer("docker.dragonflydb.io/dragonflydb/dragonfly")
        .withExposedPorts(6379)

    @Test
    fun `test server`() = testApplication {
        application { module(redis.toRedisURI()) }
        val client = createClient {
            configure()
        }

        LOG.info { "Waiting 5 seconds to receive items" }
        delay(5.seconds)

        val res = client.get(Route.VehiclePositions())
        assertEquals(HttpStatusCode.OK, res.status)
        val positions = res.body<List<VehiclePosition>>()

        assertTrue(positions.isNotEmpty(), "Response should not be empty")
    }
}

private fun GenericContainer<*>.toRedisURI() = RedisURI.create(host, firstMappedPort)
