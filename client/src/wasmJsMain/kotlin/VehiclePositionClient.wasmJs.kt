package app.trainy.geops.client

import io.ktor.client.engine.js.*

internal actual val defaultHttpClientEngineFactory: io.ktor.client.engine.HttpClientEngineFactory<*> = Js
