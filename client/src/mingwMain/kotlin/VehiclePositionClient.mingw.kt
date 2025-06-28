package app.trainy.geops.client

import io.ktor.client.engine.winhttp.WinHttp

internal actual val defaultHttpClientEngineFactory: io.ktor.client.engine.HttpClientEngineFactory<*> = WinHttp
