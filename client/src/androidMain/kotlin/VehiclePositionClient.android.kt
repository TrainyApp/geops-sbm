package app.trainy.geops.client

import com.trainyapp.cronet.Cronet
import io.ktor.client.engine.*

internal actual val defaultHttpClientEngineFactory: HttpClientEngineFactory<*> = Cronet.config {
    options {
        enableQuic(true)
        addQuicHint(defaultHost, 443, 80)
    }
}
