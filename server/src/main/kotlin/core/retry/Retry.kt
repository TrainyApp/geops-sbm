package app.trainy.geops.server.core.retry

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.times

private val LOG = KotlinLogging.logger { }

/**
 * A strategy for retrying after a failed action.
 */
@Serializable
sealed interface Retry {
    /**
     * Whether this strategy has any more retries left.
     */
    val hasNext: Boolean

    /**
     * Resets the underlying retry counter if this Retry uses an maximum for consecutive [retry] invocations.
     * This should be called after a successful [retry].
     */
    fun reset()

    /**
     * Suspends for a certain amount of time.
     */
    fun retry(): Duration
}

@Serializable
class LinearRetry(
    private val firstBackoff: Duration,
    private val maxBackoff: Duration,
    private val maxTries: Int,
    private var tries: Int = 0
) : Retry {
    init {
        require(firstBackoff.isPositive()) { "firstBackoff needs to be positive but was ${firstBackoff.inWholeMilliseconds} ms" }
        require(maxBackoff.isPositive()) { "maxBackoff needs to be positive but was ${maxBackoff.inWholeMilliseconds} ms" }
        require(maxBackoff.minus(firstBackoff).isPositive()) {
            "maxBackoff ${maxBackoff.inWholeMilliseconds} ms needs to be bigger than firstBackoff ${firstBackoff.inWholeMilliseconds} ms"
        }
        require(maxTries > 1) { "maxTries needs to be greater than 1 but was $maxTries" }
    }

    override val hasNext: Boolean
        get() = tries < maxTries

    override fun reset() {
        tries = 0
    }

    override fun retry(): Duration {
        if (!hasNext) error("max retries exceeded")

        // tries/maxTries ratio * (backOffDiff) = retryProgress
        val ratio = tries++ / (maxTries - 1).toDouble()
        val retryProgress = ratio * (maxBackoff - firstBackoff)
        val diff = firstBackoff + retryProgress

        LOG.trace { "retry attempt ${tries}, delaying for $diff" }
        return diff
    }
}

fun newRetry() = LinearRetry(2.seconds, 20.seconds, 5)
