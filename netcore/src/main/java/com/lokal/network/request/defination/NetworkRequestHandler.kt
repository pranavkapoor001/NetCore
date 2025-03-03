package com.lokal.network.request.defination

import kotlinx.coroutines.CoroutineScope
import com.lokal.network.models.BaseUseCaseNoInput
import com.lokal.network.models.BaseUseCaseWithInput
import com.lokal.network.models.CodeMsg

interface NetworkRequestHandler {

    /**
     * Sets the scope so the lifecycle is managed according to supplied [scope]
     */
    fun initNetworkRequestHandler(scope: CoroutineScope)

    /**
     * Processes the usecase without any input data and invokes either [onSuccess] or [onError]
     */
    fun <O> fetchDataNoInput(
        useCase: BaseUseCaseNoInput<O>,
        onLoading: () -> Unit,
        onSuccess: (O) -> Unit,
        onError: (CodeMsg) -> Unit,
    )

    /**
     * Processes the usecase with some input data and invokes either [onSuccess] or [onError]
     */
    fun <I, O> fetchDataWithInput(
        useCase: BaseUseCaseWithInput<I, O>,
        requestData: I,
        onLoading: () -> Unit,
        onSuccess: (O) -> Unit,
        onError: (CodeMsg) -> Unit,
    )
}