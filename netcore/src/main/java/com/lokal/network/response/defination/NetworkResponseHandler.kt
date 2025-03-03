package com.lokal.network.response.defination

import com.lokal.network.models.NetworkResponse
import com.lokal.network.models.RetryConfig
import retrofit2.Response

interface NetworkResponseHandler {
    suspend fun <T> handleApi(
        execute: suspend () -> Response<T>
    ): NetworkResponse<T>

    suspend fun <T> handleApiWithRetry(
        retryConfig: RetryConfig = RetryConfig(),
        execute: suspend () -> Response<T>
    ): NetworkResponse<T>
}