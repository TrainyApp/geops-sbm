package app.trainy.geops.client

import io.ktor.client.engine.java.Java

internal actual val defaultHttpClientEngineFactory: io.ktor.client.engine.HttpClientEngineFactory<*> = Java
