package app.trainy.geops.server.geops

import kotlin.time.Duration

object Feeds {
    const val SBAHN_MUNICH = "sbm_newsticker"
    const val HEALTHCHECK = "healthcheck"
}

sealed class Command(val operator: Operator) {
    abstract fun formatArguments(): String

    open fun formatCommand(): String = "${operator.name} ${formatArguments()}"

    enum class Operator {
        BBOX,
        BUFFER,
        SUB,
        DEL,
        GET,
        PING
    }
}

sealed class StringCommand(operator: Operator, val value: String) : Command(operator) {
    override fun formatArguments(): String = value
}

object PingCommand : Command(Operator.PING) {
    override fun formatArguments(): String = throw UnsupportedOperationException("Ping command has no arguments")
    override fun formatCommand(): String = operator.name
}

data class GetCommand(val feed: String) : StringCommand(Operator.GET, feed)
data class SubscribeCommand(val feed: String) : StringCommand(Operator.SUB, feed)
data class DeleteCommand(val feed: String) : StringCommand(Operator.DEL, feed)
data class BBoxCommand(
    val left: Int,
    val bottom: Int,
    val right: Int,
    val top: Int,
    val zoom: Int,
    val tenant: String? = null,
    val channelPrefix: String? = null
) : Command(Operator.BBOX) {
    override fun formatArguments(): String = buildString {
        append(left)
        append(' ')
        append(bottom)
        append(' ')
        append(right)
        append(' ')
        append(top)
        append(' ')
        append(zoom)

        if (tenant != null) {
            append(' ')
            append("tenant=")
            append(tenant)
        }

        if (channelPrefix != null) {
            append(' ')
            append("channel_prefix=")
            append(channelPrefix)
        }
    }
}

data class BufferCommand(val timeout: Duration, val size: Int) : Command(Operator.BUFFER) {
    override fun formatArguments(): String = "${timeout.inWholeMilliseconds} $size"
}

