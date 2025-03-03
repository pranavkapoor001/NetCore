package com.lokal.network.exts

import com.lokal.network.models.CodeMsg
import com.lokal.network.models.CustomHttpCode
import com.lokal.network.models.NetworkResponse
import com.lokal.network.models.NetworkResponse.Success
import com.lokal.network.models.UIResponse

/**
 * Converts [NetworkResponse] to [UIResponse]
 */
internal fun <T> NetworkResponse<T>.processNetworkResponse(): UIResponse<T> {
    return when (this) {
        is Success -> UIResponse.Success(this.data)
        else -> UIResponse.Error(this.getErrorFromNetworkResponse())
    }
}

/**
 * Extracts the exception from [NetworkResponse] and return a [CodeMsg]
 */
internal fun <T> NetworkResponse<T>.getErrorFromNetworkResponse(): CodeMsg {
    return when (this) {
        is NetworkResponse.Failure.ApiError -> CodeMsg(
            this.code, this.message
        )

        is NetworkResponse.Failure.NetworkError -> CodeMsg(
            CustomHttpCode.NO_NETWORK.code
        )

        is NetworkResponse.Failure.SocketTimeout -> CodeMsg(
            CustomHttpCode.SOCKET_TIMEOUT.code
        )

        is NetworkResponse.Failure.UnknownError -> CodeMsg(
            CustomHttpCode.UNKNOWN_ERROR.code
        )

        is Success -> CodeMsg(CustomHttpCode.SUCCESS.code)
    }
}