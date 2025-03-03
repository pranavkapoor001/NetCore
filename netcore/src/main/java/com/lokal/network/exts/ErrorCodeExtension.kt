package com.lokal.network.exts

import com.lokal.network.models.NoRetryErrorCodes

internal fun isRetryAvailable(code: Int): Boolean {
    return code in 400..499 && !NoRetryErrorCodes.entries.any { it.customHttpCodes.code == code }
}