<h1 align="center">Netcore</h1></br>

<p align="center">
Networking library designed to reduce boilerplate code and enforce a consistent, unified approach to making API calls with a retry mechanism.
</p>

## Including in your project
Add the repository URL and include the dependency in your project.

`settings.gradle`
```kotlin
repositories {
	/***/
	maven {
		url = uri("https://maven.pkg.github.com/pranavkapoor001/Netcore")
	}
}
```

`build.gradle.kts`
```kotlin
dependencies {
	implementation("com.lokal:netcore:1.0.0") /* See latest version in github packages */
}
```

## How to use
**`Netcore`** is designed as a singleton and independent of Android components. It supports dependency injection and can be injected into any part of the application.

#### 1. Injecting `Netcore`
Example: Constructor injection in a view model
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
	private val requestHandler: NetworkRequestHandler
	): ViewModel() { }
```
#### 2. Initializing the `NetworkRequestHandler`
```kotlin
/* The coroutine scope passed here will be used manage the scope of api calls */
requestHandler.initNetworkRequestHandler(viewModelScope) 
```
#### 3. Making an API Call: Request: `NetworkRequestHandler`
#####  3.1 API call without any request data
`BaseUseCaseNoInput<O>`
```kotlin

/**
 * A use case responsible for executing a repository method in a coroutine and returning the result as a [NetworkResponse].
 *
 * To create your own use case, extend [BaseUseCaseNoInput] and specify the output type `O`.
 * The result is returned as a [NetworkResponse<O>].
 * 
 * This particular use case fetches data from the [DataRepo] and returns it as a [NetworkResponse<Int>].
 */
class GetDataNoInputUseCase @Inject constructor(  
    private val repo: DataRepo  
): BaseUseCaseNoInput<Int>() {  
    override suspend fun process(): NetworkResponse<Int> {  
        return repo.getData()  
    }  
}
```
`fetchDataNoInput`
```kotlin
fun getData() {
	// Usecase can be injected in your class to avoid passing the repo in the constructor
	requestHandler.fetchDataNoInput(
		useCase = GetDataNoInputUseCase(repo),
		onLoading = { /* Signals loading started state */ },
		onSuccess = { responseData -> /* Same as the data type defined in output of the usecase */ },
		onFailure = { codeMsg -> }
	)
}
```
<br> </br>
##### 3.2 API call with input request data
`BaseUseCaseWithInput<I, O>`
```kotlin
/** 
 * A use case responsible for executing a repository method in a coroutine with an input parameter.
 * 
 * To create your own use case, extend [BaseUseCaseWithInput] and specify both the input type `I` and the output type `O`.
 * The result is returned as a [NetworkResponse<O>].
 *
 * This particular use case fetches data from the [DataRepo], using an input of type [String] and returning a response of type [Int].
 */
class GetDataWithInputUseCase @Inject constructor(
    private val repo: DataRepo
) : BaseUseCaseWithInput<String, Int>() {
    override suspend fun process(input: String): NetworkResponse<Int> {
        return repo.getDataWithInput(input)
    }
}
```

> **Note**: For multiple input parameters, it is recommended to use a separate data class to encapsulate the input

Example: `GetDataRequestParam`
```kotlin
// Keep this class inside your usecase
data class Input(
	@SerializedName("one") val input1: Int, 
	@SerializedName("two") val input2: String
)

// Now this class can be directly used as request body
```
`fetchDataWithInput`
```kotlin
fun getData() {
	// Usecase can be injected in your class to avoid passing the repo in the constructor
	requestHandler.fetchDataWithInput(
		useCase = GetDataWithInputUseCase(repo),
		requestData = GetDataWithInputUseCase.Input("1", "Meow"), // Input data
		onLoading = { /* Signals loading started state */ },
		onSuccess = { responseData -> /* Same as the data type defined in output of the usecase */ },
		onFailure = { codeMsg -> }
	)
}
```

#### 4. Making an API Call: Response: `NetworkResponseHandler`
`Repository`
```kotlin
class MyRepository @Inject constructor(
	private val handler: NetworkResponseHandler
) {
	
	// No retry
	fun getDataWithoutRetry(input: String) {
		return handler.handleApi { /* Processes the response and returns NetworkResponse<T> */
			api.getData(input)
		}
	}

	// With retry
	fun getDataWithRetry(input: String) {
		return handler.handleApiWithRetry(RetryConfig()) { /* Processes the response with retry mechanism and returns NetworkResponse<T> */
			api.getData(input)
		}
	}
}
```

## `RetryConfig`
Defines the behaviour of retry mechanism

```kotlin
/**
 * Configuration for retry behavior.
 *
 * @property retryEnabled Whether retries are enabled. Defaults to `true`.
 * @property retryStrategy The strategy for calculating retry delays. Defaults to [RetryStrategy.EXPONENTIAL].
 *   - [RetryStrategy.CONSTANT]: Constant delay between retries.
 *   - [RetryStrategy.LINEAR]: Linearly increasing delay.
 *   - [RetryStrategy.EXPONENTIAL]: Exponentially increasing delay.
 * @property maxRetryCount The maximum number of retries. Defaults to `3`.
 * @property initialDelayForRetry The initial delay (in ms) before the first retry. Defaults to `500 ms`.
 */
data class RetryConfig(
    val retryEnabled: Boolean = true,
    val retryStrategy: RetryStrategy = RetryStrategy.EXPONENTIAL,
    val maxRetryCount: Int = 3,
    val initialDelayForRetry: Long = 500,
)
```
> **Note**: Retries will be skipped for `4xx` errors and any custom error codes listed in `NoRetryErrorCodes`



## `NetworkResponse`

* Represents the result of a network operation, which can either be a success or a failure.
* `Success`: Indicates a successful response with the returned data.
* `Failure`: Represents different types of errors that may occur during the network operation.
  * `ApiError` Error with an HTTP status code and message.
  * `NetworkError` Network-related error (e.g., no connectivity).
  * `SocketTimeout` Timeout error due to slow network or long API response time.
  * `UnknownError` Unexpected error, such as JSON parsing failure or other exceptions

## `NoRetryErrorCodes`
```kotlin
BAD_REQUEST: 400
UNAUTHORIZED: 401
ALREADY_REGISTERED_IN_OTHER_APP: 403
CONTENT_NOT_FOUND: 404
NOT_ALLOWED: 405
RE_GENERATE_OTP: 409,  
TOO_MANY_REQUESTS: 429,  
UNKNOWN_ERROR: 603
```
> **Note**:  Custom error codes will be customisable in future versions of this library.