package com.lokal.network.models

import androidx.annotation.Keep

sealed class UIResponse<out O> {

    @Keep
    class Loading<O> : UIResponse<O>()

    @Keep
    data class Success<out O>(val data: O) : UIResponse<O>()

    @Keep
    data class Error<out O>(val error: CodeMsg) : UIResponse<O>()
}