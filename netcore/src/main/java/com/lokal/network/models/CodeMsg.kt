package com.lokal.network.models

import androidx.annotation.Keep

@Keep
data class CodeMsg(
    val code: Int,
    val msg: String? = null,
)