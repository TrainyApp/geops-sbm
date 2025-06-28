import app.trainy.geops.client.VehiclePositionClient
import app.trainy.geops.types.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock

class ClientTest {

    @JsName("testGetPositions")
    @Test
    fun `test get positions`() = runTest {
        val items = generateSequence { mockPosition() }.take(10).toList()
        val client = VehiclePositionClient(mockEngine(items))

        assertEquals(items, client.getPositions())
    }

    @JsName("testGetPositionsReturnsNonEmptyList")
    @Test
    fun `test get position`() = runTest {
        val item = mockPosition()
        val client = VehiclePositionClient(mockEngine(item))

        assertEquals(client.getPosition("123"), item)
    }

    @JsName("testGetPositionReturnsNull")
    @Test
    fun `test get position returns null`() = runTest {
        val client = VehiclePositionClient(mock404Engine())

        assertNull(client.getPosition("dasdsa"))
    }
}

private inline fun <reified T> mockEngine(response: T) = MockEngine { request ->
    val headers = headersOf(HttpHeaders.ContentType to listOf(ContentType.Application.ProtoBuf.toString()))
    respond(ProtoBuf.encodeToByteArray(response), HttpStatusCode.OK, headers)
}.asFactory()

private fun mock404Engine() = MockEngine {
    respondError(HttpStatusCode.NotFound)
}.asFactory()

private class MockEngineFactory(private val engine: MockEngine) : HttpClientEngineFactory<MockEngineConfig> {
    override fun create(block: MockEngineConfig.() -> Unit): HttpClientEngine = engine
}

private fun MockEngine.asFactory() = MockEngineFactory(this)

private fun mockPosition() = VehiclePosition(
    VehiclePosition.Status.REALTIME,
    Clock.System.now(),
    Vehicle("123", "123", "123"),
    Journey(1, 1, "S1", LineColor(Color(0x1), Color(0x2), Color(0x4))),
    Coordinate(1.0, 1.0)
)
