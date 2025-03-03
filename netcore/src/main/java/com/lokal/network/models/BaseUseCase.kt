package com.lokal.network.models

abstract class BaseUseCaseNoInput<out O> {
    abstract suspend fun process(): NetworkResponse<O>
}

abstract class BaseUseCaseWithInput<in I, out O> {
    abstract suspend fun process(input: I): NetworkResponse<O>
}