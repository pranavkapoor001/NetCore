package com.lokal.network.response.implementation

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.delay
import com.lokal.network.exts.getErrorFromNetworkResponse
import com.lokal.network.exts.isRetryAvailable
import com.lokal.network.models.NetworkResponse
import com.lokal.network.models.RetryConfig
import com.lokal.network.response.defination.NetworkResponseHandler
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

internal class NetworkResponseHandlerImpl @Inject constructor() : NetworkResponseHandler {
    override suspend fun <T> handleApi(
        execute: suspend () -> Response<T> // takes repo call
    ): NetworkResponse<T> {
        return try {
            val response = execute()
            response.getSuccessOrThrow()
        } catch (e: Throwable) {
            logException(e)
            e.getNetworkResponseFromException()
        }
    }

    override suspend fun <T> handleApiWithRetry(
        retryConfig: RetryConfig,
        execute: suspend () -> Response<T>
    ): NetworkResponse<T> {
        // Get response
        var response = handleApi { execute() }

        // Check if failed and retry should be done
        if (shouldRetry(retryConfig.getRetryAttempt(), retryConfig, response)) {
            // Calculate delay for retrying
            val currentDelay = RetryConfig.calculateDelay(
                retryConfig.getRetryAttempt(),
                retryConfig.initialDelayForRetry,
                retryConfig.retryStrategy
            )

            // Backoff time
            delay(currentDelay)

            // Retry
            retryConfig.incRetryAttempt()
            response = handleApiWithRetry(retryConfig) { execute() }
        }

        return response
    }
}

/**
 * Converts [Response] to either [NetworkResponse.Success] or [NetworkResponse.Failure.ApiError]
 */
private fun <T> Response<T>.getSuccessOrThrow(): NetworkResponse<T> {
    val body = this.body()
    return if (this.isSuccessful && body != null) {
        NetworkResponse.Success(body)
    } else {
        throw HttpException(this)
    }
}

private fun <T> Response<T>.getCustomErrorResponse(): String? {
    return this.errorBody()?.charStream()?.let {
        Gson().fromJson(it, ErrorResponse::class.java)
    }?.msg
}


data class ErrorResponse(
    @SerializedName("message")
    val msg: String,
)

/**
 * Converts [Exception] to specific type of [NetworkResponse] error
 */
private fun <T> Throwable.getNetworkResponseFromException(): NetworkResponse<T> {
    return when (this) {
        is SocketTimeoutException -> NetworkResponse.Failure.SocketTimeout(this)

        is IOException -> NetworkResponse.Failure.NetworkError(this)

        is HttpException -> {
            val errorResponse = this.response()?.getCustomErrorResponse()
            NetworkResponse.Failure.ApiError(
                code = this.code(),
                message = errorResponse ?: this.message()
            )
        }

        else -> NetworkResponse.Failure.UnknownError(this)
    }
}

private fun <T> shouldRetry(
    retryAttemptCount: Int,
    retryConfig: RetryConfig,
    response: NetworkResponse<T>
): Boolean {
    return (response !is NetworkResponse.Success) &&
            (retryConfig.retryEnabled) &&
            (retryAttemptCount < retryConfig.maxRetryCount) &&
            (isRetryAvailable(response.getErrorFromNetworkResponse().code))
}

private fun logException(e: Throwable) {
    Firebase.crashlytics.recordException(e)
}