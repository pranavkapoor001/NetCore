package com.lokal.network.models

/**
 * Defines the error codes on which we should not retry the API call
 */
enum class NoRetryErrorCodes(val customHttpCodes: CustomHttpCode) {
    BAD_REQUEST_400(CustomHttpCode.BAD_REQUEST),
    UNAUTHORIZED_401(CustomHttpCode.UNAUTHORIZED),
    ALREADY_REGISTERED_IN_OTHER_APP_403(CustomHttpCode.ALREADY_REGISTERED_IN_OTHER_APP),
    CONTENT_NOT_FOUND_404(CustomHttpCode.CONTENT_NOT_FOUND),
    NOT_ALLOWED_405(CustomHttpCode.NOT_ALLOWED),
    RE_GENERATE_OTP_409(CustomHttpCode.RE_GENERATE_OTP),
    TOO_MANY_REQUESTS_429(CustomHttpCode.TOO_MANY_REQUESTS),
    UNKNOWN_ERROR_603(CustomHttpCode.UNKNOWN_ERROR),
}