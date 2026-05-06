package app.trainy.geops.client

import com.trainyapp.cronet.Cronet
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.OkHttp

private val isCronetAvailable = try {
    Class.forName("com.trainyapp.cronet.Cronet")
    true
}  catch (_: ClassNotFoundException) {
    false
}

internal actual val defaultHttpClientEngineFactory: HttpClientEngineFactory<*> = if (isCronetAvailable) {
    Cronet.config {
        options {
            enableQuic(true)
            addQuicHint(defaultHost, 443, 80)
        }
    }
} else {
    OkHttp
}