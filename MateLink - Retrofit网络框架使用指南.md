# MateLink - Retrofit网络框架使用指南

## 🌐 概述

MateLink的Retrofit网络框架提供了完整的网络请求解决方案，包括认证、缓存、错误处理、重试机制、网络监控等功能，确保应用的网络通信稳定可靠。

## ✨ 核心特性

- 🔐 **自动认证**：Token自动管理和刷新
- 🔄 **智能重试**：自动重试失败的请求
- 💾 **缓存策略**：HTTP缓存和离线支持
- 📊 **网络监控**：实时网络状态监控
- 🛡️ **错误处理**：统一的错误处理和用户提示
- 📝 **请求日志**：详细的网络请求日志
- ⚡ **性能优化**：连接池、超时配置等

## 🏗️ 架构设计

```
Network Layer
├── ApiClient (Retrofit配置)
├── Interceptors (拦截器)
│   ├── AuthInterceptor (认证)
│   ├── LoggingInterceptor (日志)
│   ├── NetworkInterceptor (网络检查)
│   ├── CacheInterceptor (缓存)
│   └── RetryInterceptor (重试)
├── APIs (接口定义)
│   ├── AuthApi (认证接口)
│   ├── SpaceApi (空间接口)
│   ├── ChatApi (聊天接口)
│   └── MomentApi (朋友圈接口)
├── Models (数据模型)
├── Error Handling (错误处理)
└── Monitoring (网络监控)
```

## 🚀 快速开始

### 1. 基础使用

```kotlin
// 在Repository中使用API
@Singleton
class UserRepository @Inject constructor(
    private val authApi: AuthApi,
    private val networkUtils: NetworkUtils,
    private val errorHandler: NetworkErrorHandler
) {
    
    suspend fun login(email: String, password: String): Result<UserProfile> {
        return networkUtils.safeApiCall {
            authApi.login(LoginRequest(email, password))
        }.map { response ->
            response.user
        }
    }
}
```

### 2. 在ViewModel中使用

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<Result<UserProfile>?>(null)
    val loginState: StateFlow<Result<UserProfile>?> = _loginState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            _loginState.value = userRepository.login(email, password)
        }
    }
}
```

### 3. 在Compose UI中使用

```kotlin
@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    val loginState by viewModel.loginState.collectAsState()
    
    when (loginState) {
        is Result.Loading -> {
            CircularProgressIndicator()
        }
        is Result.Success -> {
            Text("登录成功：${loginState.data.username}")
        }
        is Result.Error -> {
            Text(
                text = "登录失败：${loginState.message}",
                color = MaterialTheme.colorScheme.error
            )
        }
        null -> {
            // 初始状态
        }
    }
}
```

## 🔧 详细功能

### 1. 认证系统

#### Token自动管理
```kotlin
// Token会自动添加到请求头
// 当token过期时，会自动刷新
// 刷新失败时，会清除token并跳转登录

class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 自动添加Authorization头
        // 处理401响应，自动刷新token
    }
}
```

#### 使用示例
```kotlin
// 登录后保存token
val loginResponse = authApi.login(request)
tokenManager.saveTokens(
    loginResponse.accessToken,
    loginResponse.refreshToken
)

// 检查登录状态
if (tokenManager.isLoggedIn()) {
    // 已登录
} else {
    // 需要登录
}
```

### 2. 错误处理

#### 统一错误处理
```kotlin
// 所有网络错误都会被统一处理
val result = networkUtils.safeApiCall {
    api.someRequest()
}

result.onError { exception, message ->
    // 显示错误提示
    showErrorMessage(message)
}
```

#### 自定义错误处理
```kotlin
try {
    val response = api.someRequest()
    // 处理成功响应
} catch (e: Exception) {
    val error = errorHandler.handleError(e)
    when (error.type) {
        ErrorType.UNAUTHORIZED -> {
            // 跳转登录页面
        }
        ErrorType.NO_NETWORK -> {
            // 显示网络错误提示
        }
        else -> {
            // 显示通用错误提示
        }
    }
}
```

### 3. 网络监控

#### 监控网络状态
```kotlin
@Composable
fun NetworkAwareContent() {
    val networkMonitor = hiltViewModel<NetworkMonitor>()
    val networkStatus by networkMonitor.networkStatus.collectAsState()
    
    when (networkStatus) {
        NetworkStatus.DISCONNECTED -> {
            Text("网络未连接")
        }
        NetworkStatus.CONNECTED_WIFI -> {
            Text("WiFi已连接")
        }
        NetworkStatus.CONNECTED_MOBILE -> {
            Text("移动网络已连接")
        }
        else -> {
            Text("网络状态：$networkStatus")
        }
    }
}
```

### 4. 缓存策略

#### HTTP缓存
```kotlin
// GET请求自动缓存5分钟
@GET("api/data")
suspend fun getData(): Response<DataResponse>

// 手动控制缓存
@GET("api/data")
@Headers("Cache-Control: max-age=3600") // 1小时缓存
suspend fun getCachedData(): Response<DataResponse>
```

#### 清除缓存
```kotlin
// 清除所有缓存
apiClient.clearCache()

// 获取缓存大小
val cacheSize = apiClient.getCacheSize()
```

### 5. 文件上传

#### 单文件上传
```kotlin
suspend fun uploadFile(file: File): Result<FileUploadResponse> {
    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
    val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)
    
    return networkUtils.safeApiCall {
        chatApi.uploadFile(multipartBody, "image")
    }
}
```

#### 多文件上传
```kotlin
suspend fun uploadMultipleFiles(files: List<File>): Result<List<FileUploadResponse>> {
    val results = mutableListOf<FileUploadResponse>()
    
    files.forEach { file ->
        when (val result = uploadFile(file)) {
            is Result.Success -> results.add(result.data)
            is Result.Error -> return result
        }
    }
    
    return Result.Success(results)
}
```

## 📊 性能优化

### 1. 连接池配置

```kotlin
// 在ApiClient中已配置
private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .callTimeout(60, TimeUnit.SECONDS)
    .build()
```

### 2. 请求优化

```kotlin
// 根据网络质量调整请求策略
val strategy = networkQualityMonitor.getRecommendedRequestStrategy()

when (strategy) {
    RequestStrategy.CONSERVATIVE -> {
        // 只加载必要内容，压缩图片
    }
    RequestStrategy.OPTIMIZED -> {
        // 适度优化，减少并发请求
    }
    RequestStrategy.NORMAL -> {
        // 正常加载
    }
}
```

### 3. 分页加载

```kotlin
suspend fun loadMessages(
    spaceId: String,
    page: Int = 1,
    pageSize: Int = 20
): Result<PageResponse<Message>> {
    return networkUtils.handlePagedRequest {
        chatApi.getMessages(spaceId, page = page, pageSize = pageSize)
    }
}
```

## 🐛 错误处理最佳实践

### 1. Repository层错误处理

```kotlin
class ChatRepository @Inject constructor(
    private val chatApi: ChatApi,
    private val networkUtils: NetworkUtils
) {
    
    suspend fun sendMessage(request: SendMessageRequest): Result<Message> {
        return networkUtils.safeApiCallWithRetry(maxRetries = 3) {
            chatApi.sendMessage(request)
        }
    }
}
```

### 2. UI层错误显示

```kotlin
@Composable
fun ErrorHandlingExample() {
    val result by viewModel.result.collectAsState()
    
    when (result) {
        is Result.Error -> {
            // 显示错误提示
            LaunchedEffect(result) {
                showSnackbar(result.message)
            }
        }
        is Result.Success -> {
            // 显示成功内容
        }
        is Result.Loading -> {
            // 显示加载状态
        }
    }
}
```

### 3. 全局错误处理

```kotlin
// 在Application中设置全局异常处理
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        
        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            // 记录网络相关异常
            if (exception is IOException || exception is HttpException) {
                // 发送到崩溃分析平台
            }
        }
    }
}
```

## 📱 实际使用示例

### 登录流程完整示例

```kotlin
// 1. Repository实现
@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val networkUtils: NetworkUtils
) {
    
    suspend fun login(email: String, password: String): Result<UserProfile> {
        return networkUtils.safeApiCall {
            authApi.login(LoginRequest(email, password))
        }.onSuccess { response ->
            // 保存token
            tokenManager.saveTokens(
                response.accessToken,
                response.refreshToken
            )
        }.map { response ->
            response.user
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return networkUtils.safeApiCall {
            authApi.logout()
        }.onSuccess {
            // 清除本地token
            tokenManager.clearTokens()
        }
    }
}

// 2. ViewModel实现
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<Result<UserProfile>?>(null)
    val loginState: StateFlow<Result<UserProfile>?> = _loginState.asStateFlow()
    
    val networkStatus = networkMonitor.networkStatus
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            _loginState.value = authRepository.login(email, password)
        }
    }
}

// 3. UI实现
@Composable
fun LoginScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val loginState by viewModel.loginState.collectAsState()
    val networkStatus by viewModel.networkStatus.collectAsState()
    
    Column {
        // 网络状态提示
        if (networkStatus == NetworkStatus.DISCONNECTED) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "网络未连接",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        // 登录表单
        LoginForm(
            onLoginClick = { email, password ->
                viewModel.login(email, password)
            },
            isLoading = loginState is Result.Loading
        )
        
        // 错误提示
        loginState?.let { state ->
            when (state) {
                is Result.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is Result.Success -> {
                    Text(
                        text = "登录成功",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                else -> {}
            }
        }
    }
}
```

## 🔧 高级配置

### 1. 自定义拦截器

```kotlin
class CustomInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // 自定义逻辑
        val newRequest = request.newBuilder()
            .header("Custom-Header", "value")
            .build()
        
        return chain.proceed(newRequest)
    }
}
```

### 2. 动态BaseUrl

```kotlin
class DynamicBaseUrlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 根据条件选择不同的BaseUrl
        val newUrl = when {
            isTestEnvironment() -> "https://test-api.matelink.com/"
            isProductionEnvironment() -> "https://api.matelink.com/"
            else -> originalRequest.url.toString()
        }
        
        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()
        
        return chain.proceed(newRequest)
    }
}
```

### 3. 请求加密

```kotlin
class EncryptionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        if (needsEncryption(request)) {
            // 加密请求体
            val encryptedBody = encryptRequestBody(request.body)
            val newRequest = request.newBuilder()
                .method(request.method, encryptedBody)
                .build()
            
            val response = chain.proceed(newRequest)
            
            // 解密响应体
            return decryptResponse(response)
        }
        
        return chain.proceed(request)
    }
}
```

## 📊 监控和调试

### 1. 网络请求监控

```kotlin
// 在开发环境启用详细日志
class NetworkMonitoringInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()
        
        val response = chain.proceed(request)
        
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        // 记录请求统计
        logRequestStats(request, response, duration)
        
        return response
    }
}
```

### 2. 性能分析

```kotlin
class NetworkPerformanceAnalyzer {
    fun analyzeRequest(
        url: String,
        method: String,
        duration: Long,
        responseSize: Long
    ) {
        // 分析请求性能
        when {
            duration > 5000 -> logSlowRequest(url, duration)
            responseSize > 1024 * 1024 -> logLargeResponse(url, responseSize)
        }
    }
}
```

## 🛡️ 安全最佳实践

### 1. 证书锁定

```kotlin
val certificatePinner = CertificatePinner.Builder()
    .add("api.matelink.com", "sha256/XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX=")
    .build()

val okHttpClient = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```

### 2. 请求签名

```kotlin
class SignatureInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // 生成请求签名
        val signature = generateSignature(request)
        
        val newRequest = request.newBuilder()
            .header("X-Signature", signature)
            .header("X-Timestamp", System.currentTimeMillis().toString())
            .build()
        
        return chain.proceed(newRequest)
    }
}
```

## 🎯 总结

MateLink的Retrofit网络框架提供了：

1. **完整的功能覆盖**：认证、缓存、监控、错误处理
2. **高可用性**：重试机制、网络检查、降级策略
3. **易于使用**：统一的API接口、简洁的调用方式
4. **高性能**：连接池、缓存、请求优化
5. **安全可靠**：认证管理、错误处理、日志记录

通过这套网络框架，MateLink可以确保稳定可靠的网络通信，为用户提供流畅的使用体验。

---

**建议**：在实际使用中，根据具体业务需求调整超时时间、缓存策略和重试次数等配置参数。
