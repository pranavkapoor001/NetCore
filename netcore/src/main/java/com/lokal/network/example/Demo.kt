package com.lokal.network.example

import androidx.annotation.Keep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import com.lokal.network.models.BaseUseCaseWithInput
import com.lokal.network.models.NetworkResponse
import com.lokal.network.request.defination.NetworkRequestHandler
import com.lokal.network.response.defination.NetworkResponseHandler
import com.lokal.network.response.implementation.NetworkResponseHandlerImpl
import retrofit2.Response
import javax.inject.Inject

internal class Demo @Inject constructor(
    private val requestHandler: NetworkRequestHandler
) {

    private val scope = CoroutineScope(Job())
    fun init() {
        requestHandler.initNetworkRequestHandler(scope)
    } // TODO: Better way ?

    fun getSomeData() {
        requestHandler.fetchDataWithInput(
            useCase = DemoUseCase(DemoRepo()),
            requestData = DemoUseCase.Input("Meow"),
            onLoading = {
                // Set something to UI
            },
            onSuccess = {
                // Set success
            },
            onError = {
                // Show error
            }
        )
    }
}

/**---- Domain layer ----*/

class DemoUseCase(private val repo: DemoRepo) : BaseUseCaseWithInput<DemoUseCase.Input, Int>() {
    override suspend fun process(input: Input): NetworkResponse<Int> {
        return repo.getData(input.msg) // Do any data manipulation here
    }

    @Keep
    data class Input(
        val msg: String,
    )
}

/**---- Data layer ----*/

// Repo
interface IDemoRepo {
    suspend fun getData(input: String): NetworkResponse<Int>
}

// Repo Impl
class DemoRepo : IDemoRepo, NetworkResponseHandler by NetworkResponseHandlerImpl() {
    // TODO: Inject these
    private val api = APIImpl()

    override suspend fun getData(input: String): NetworkResponse<Int> {
        return handleApi {
            api.getData(input)
        }
    }
}

/**---- API interface ----*/

interface API {
    fun getData(msg: String): Response<Int>
}

class APIImpl : API {
    override fun getData(msg: String): Response<Int> {
        TODO("Not yet implemented")
    }

}