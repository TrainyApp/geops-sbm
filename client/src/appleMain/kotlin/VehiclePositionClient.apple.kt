package app.trainy.geops.client

import io.ktor.client.engine.darwin.Darwin

internal actual val defaultHttpClientEngineFactory: io.ktor.client.engine.HttpClientEngineFactory<*> = Darwin
