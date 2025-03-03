package com.lokal.network.request.implementation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.lokal.network.exts.processNetworkResponse
import com.lokal.network.models.BaseUseCaseNoInput
import com.lokal.network.models.BaseUseCaseWithInput
import com.lokal.network.models.CodeMsg
import com.lokal.network.models.NetworkResponse
import com.lokal.network.models.UIResponse
import com.lokal.network.request.defination.NetworkRequestHandler
import javax.inject.Inject

internal class NetworkRequestHandlerImpl @Inject constructor(): NetworkRequestHandler {

    private var requestScope: CoroutineScope? = null

    override fun initNetworkRequestHandler(scope: CoroutineScope) {
        requestScope = scope
    }


    override fun <O> fetchDataNoInput(
        useCase: BaseUseCaseNoInput<O>,
        onLoading: () -> Unit,
        onSuccess: (O) -> Unit,
        onError: (CodeMsg) -> Unit,
    ) {
        onLoading()
        processNetworkRequest(
            request = { useCase.process() },
            response = {
                when (it) {
                    is UIResponse.Success -> onSuccess(it.data)
                    is UIResponse.Loading -> onLoading()
                    is UIResponse.Error -> onError(it.error)
                }
            },
        )
    }

    override fun <I, O> fetchDataWithInput(
        useCase: BaseUseCaseWithInput<I, O>,
        requestData: I,
        onLoading: () -> Unit,
        onSuccess: (O) -> Unit,
        onError: (CodeMsg) -> Unit,
    ) {
        onLoading()
        processNetworkRequest(
            request = { useCase.process(requestData) },
            response = {
                when (it) {
                    is UIResponse.Success -> onSuccess(it.data)
                    is UIResponse.Loading -> onLoading()
                    is UIResponse.Error -> onError(it.error)
                }
            })
    }

    private inline fun <O> processNetworkRequest(
        crossinline request: suspend () -> NetworkResponse<O>,
        crossinline response: (res: UIResponse<O>) -> Unit,
    ) {
        val scope = requestScope ?: throw Exception("initNetworkRequestHandler() not called!")

        scope.launch(Dispatchers.IO) { // Process the request work on IO
            val result = request()
            withContext(Dispatchers.Main) { // Switch to Main since we will set this to some observable
                response(result.processNetworkResponse())
            }
        }
    }
}