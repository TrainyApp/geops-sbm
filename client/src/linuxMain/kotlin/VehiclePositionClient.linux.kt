package app.trainy.geops.client

import io.ktor.client.engine.curl.Curl

internal actual val defaultHttpClientEngineFactory: io.ktor.client.engine.HttpClientEngineFactory<*> = Curl
