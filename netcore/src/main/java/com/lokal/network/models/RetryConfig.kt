package com.lokal.network.models

import androidx.annotation.Keep
import kotlin.math.pow

@Keep
data class RetryConfig(
    val retryEnabled: Boolean = DEFAULT_RETRY_ENABLED,
    val retryStrategy: RetryStrategy = RetryStrategy.EXPONENTIAL,
    val maxRetryCount: Int = DEFAULT_MAX_RETRY_COUNT,
    val initialDelayForRetry: Long = INITIAL_DELAY,
) {
    private var retryAttemptCount = INITIAL_RETRY_ATTEMPT_COUNT
    fun getRetryAttempt() = retryAttemptCount
    fun incRetryAttempt() {
        retryAttemptCount++
    }

    @Keep
    enum class RetryStrategy {
        CONSTANT,
        LINEAR,
        EXPONENTIAL,
    }

    companion object {

        // Defaults
        private const val INITIAL_DELAY = 500L // ms
        private const val DEFAULT_MAX_RETRY_COUNT = 3
        private const val DEFAULT_RETRY_ENABLED = true
        private const val INITIAL_RETRY_ATTEMPT_COUNT = 0

        /**
         * Returns the time in milliseconds we should wait before retrying the API call again
         * @param retryStrategy Defines the backoff policy
         */
        fun calculateDelay(
            tryNumber: Int,
            initialIntervalMilli: Long = INITIAL_DELAY,
            retryStrategy: RetryStrategy
        ): Long {
            return when (retryStrategy) {
                RetryStrategy.CONSTANT -> initialIntervalMilli
                RetryStrategy.LINEAR -> initialIntervalMilli * tryNumber
                RetryStrategy.EXPONENTIAL -> 2.0.pow(tryNumber).toLong()
            }
        }
    }

}