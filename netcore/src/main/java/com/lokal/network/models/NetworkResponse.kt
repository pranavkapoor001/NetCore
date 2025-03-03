package com.lokal.network.models

import androidx.annotation.Keep
import java.io.IOException
import java.net.SocketTimeoutException


// Parent
sealed interface NetworkResponse<out O> {

    // Successful response: with body data
    @Keep
    data class Success<out O>(val data: O) : NetworkResponse<O>

    // Types of errors
    sealed interface Failure<T> : NetworkResponse<T> {

        // Failed response: error code and msg without body data
        @Keep
        data class ApiError<out O>(val code: Int, val message: String) : NetworkResponse<O>

        // Failed request: due to no network connectivity
        @Keep
        data class NetworkError<out O>(val error: IOException) : NetworkResponse<O>

        // Failed request: due to no slow connectivity or api taking too long
        @Keep
        data class SocketTimeout<out O>(val error: SocketTimeoutException) : NetworkResponse<O>

        // Unexpected exception: For example: json parsing error
        @Keep
        data class UnknownError<out O>(val throwable: Throwable) : NetworkResponse<O>
    }
}