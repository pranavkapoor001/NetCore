package com.lokal.network.models

/**
 * Defines all the error codes that we consume
 */
enum class CustomHttpCode(val code: Int) {
    SUCCESS(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    ALREADY_REGISTERED_IN_OTHER_APP(403),
    CONTENT_NOT_FOUND(404),
    NOT_ALLOWED(405),
    RE_GENERATE_OTP(409),
    TOO_MANY_REQUESTS(429),
    NO_NETWORK(600),
    SOCKET_TIMEOUT(601),
    UNKNOWN_ERROR(603), // Eg: Parsing exception
}