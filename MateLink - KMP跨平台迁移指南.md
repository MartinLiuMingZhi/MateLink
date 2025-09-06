# MateLink - KMP跨平台迁移指南

## 📋 文档概述

本文档为MateLink友联项目提供从Android单平台架构向Kotlin Multiplatform（KMP）跨平台架构的完整迁移指南。基于当前的模块化架构设计，提供渐进式迁移方案，确保在保持Android开发效率的同时，为未来跨平台扩展预留最大灵活性。

## 🎯 迁移目标

- **短期目标**：保持Android开发优先，架构KMP友好
- **中期目标**：核心业务逻辑跨平台共享
- **长期目标**：完整的iOS/Android双平台支持

## 📊 当前架构KMP兼容性评估

### ✅ **高兼容性模块（可直接迁移）**

| 模块 | KMP兼容度 | 迁移工作量 | 说明 |
|------|-----------|------------|------|
| **feature/*/domain** | 🟢 **100%** | 几乎为0 | 纯业务逻辑，无平台依赖 |
| **core/common** | 🟢 **95%** | 极小 | 工具类、扩展函数、常量 |
| **core/data/model** | 🟢 **100%** | 几乎为0 | 纯数据类，完全通用 |
| **core/data/repository接口** | 🟢 **100%** | 几乎为0 | Repository接口定义 |

### 🟡 **中兼容性模块（需要适配）**

| 模块 | KMP兼容度 | 迁移工作量 | 迁移方案 |
|------|-----------|------------|----------|
| **core/network** | 🟡 **80%** | 中等 | Retrofit → Ktor Client |
| **core/database** | 🟡 **70%** | 中等 | Room → SQLDelight |
| **core/ui** | 🟡 **90%** | 小 | Compose → Compose Multiplatform |
| **feature/*/data实现** | 🟡 **75%** | 中等 | 使用expect/actual模式 |

### 🔴 **平台特定模块（保持独立）**

| 模块 | 处理方式 |
|------|----------|
| **app** | Android/iOS各自独立实现 |
| **core/di** | Hilt(Android) / Koin(共享) |

## 🛣️ 渐进式迁移路径

### 阶段1：架构准备（当前阶段）
**时间：持续进行**
**目标：Android开发优先，KMP友好设计**

#### 1.1 设计原则
```kotlin
// ✅ 推荐：接口化设计
interface UserRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getProfile(): Result<UserProfile>
}

// ✅ 推荐：纯业务逻辑
class LoginUseCase(
    private val userRepository: UserRepository,
    private val validator: EmailValidator
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (!validator.isValid(email)) {
            return Result.Error("Invalid email format")
        }
        return userRepository.login(email, password)
    }
}

// ✅ 推荐：纯数据模型
@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val createdAt: Long
)
```

#### 1.2 避免的反模式
```kotlin
// ❌ 避免：直接Android依赖
class UserManager(private val context: Context) {
    fun saveUser(user: User) {
        val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        // 平台特定实现
    }
}

// ✅ 推荐：抽象平台依赖
interface PreferencesManager {
    suspend fun saveUser(user: User)
    suspend fun getUser(): User?
}

class AndroidPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferencesManager {
    // Android特定实现
}
```

### 阶段2：共享模块创建（迁移准备）
**时间：2-3周**
**目标：创建跨平台共享基础**

#### 2.1 项目结构调整
```
MateLink/
├── shared/                       # KMP共享模块
│   ├── build.gradle.kts         # KMP配置
│   └── src/
│       ├── commonMain/kotlin/   # 跨平台共享代码
│       │   ├── domain/          # 业务逻辑层
│       │   ├── data/            # 数据层接口
│       │   └── common/          # 通用工具
│       ├── androidMain/kotlin/  # Android特定实现
│       └── iosMain/kotlin/      # iOS特定实现
├── androidApp/                  # Android应用
└── iosApp/                      # iOS应用（未来）
```

#### 2.2 共享模块配置
```kotlin
// shared/build.gradle.kts
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization")
}

kotlin {
    android()
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "MateLink Shared Module"
        homepage = "https://github.com/yourusername/matelink"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "shared"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("io.ktor:ktor-client-core:2.3.4")
                implementation("com.russhwolf:multiplatform-settings:1.0.0")
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:2.3.4")
            }
        }
        
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.4")
            }
        }
    }
}
```

#### 2.3 迁移优先级
1. **第一批**：domain层业务逻辑
2. **第二批**：数据模型和工具类
3. **第三批**：Repository接口定义
4. **第四批**：网络层抽象

### 阶段3：核心功能迁移（逐步实施）
**时间：4-6周**
**目标：核心业务逻辑跨平台共享**

#### 3.1 业务逻辑迁移
```kotlin
// shared/src/commonMain/kotlin/domain/usecase/LoginUseCase.kt
class LoginUseCase(
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager
) {
    suspend operator fun invoke(
        email: String, 
        password: String
    ): Result<User> {
        return try {
            val user = userRepository.login(email, password)
            preferencesManager.saveAuthToken(user.token)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed")
        }
    }
}
```

#### 3.2 网络层迁移
```kotlin
// shared/src/commonMain/kotlin/data/network/ApiClient.kt
class ApiClient {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }
    
    suspend fun login(request: LoginRequest): LoginResponse {
        return httpClient.post("$BASE_URL/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
```

#### 3.3 数据存储迁移
```kotlin
// shared/src/commonMain/kotlin/data/local/PreferencesManager.kt
expect class PreferencesManager {
    suspend fun saveAuthToken(token: String)
    suspend fun getAuthToken(): String?
    suspend fun clearAuthToken()
}

// shared/src/androidMain/kotlin/data/local/PreferencesManager.kt
actual class PreferencesManager(context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    actual suspend fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }
    
    actual suspend fun getAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }
    
    actual suspend fun clearAuthToken() {
        prefs.edit().remove("auth_token").apply()
    }
}

// shared/src/iosMain/kotlin/data/local/PreferencesManager.kt
actual class PreferencesManager {
    actual suspend fun saveAuthToken(token: String) {
        NSUserDefaults.standardUserDefaults.setObject(token, "auth_token")
    }
    
    actual suspend fun getAuthToken(): String? {
        return NSUserDefaults.standardUserDefaults.stringForKey("auth_token")
    }
    
    actual suspend fun clearAuthToken() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey("auth_token")
    }
}
```

### 阶段4：UI层跨平台（Compose Multiplatform）
**时间：3-4周**
**目标：UI代码跨平台共享**

#### 4.1 共享UI组件
```kotlin
// shared/src/commonMain/kotlin/ui/components/LoginScreen.kt
@Composable
fun LoginScreen(
    state: LoginState,
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("邮箱") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colors.onPrimary
                )
            } else {
                Text("登录")
            }
        }
    }
}
```

#### 4.2 状态管理
```kotlin
// shared/src/commonMain/kotlin/presentation/LoginViewModel.kt
class LoginViewModel(
    private val loginUseCase: LoginUseCase
) {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = result.data,
                        isLoggedIn = true
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}

data class LoginState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)
```

### 阶段5：平台特定实现（iOS支持）
**时间：4-5周**
**目标：完整的iOS应用支持**

#### 5.1 iOS应用结构
```swift
// iosApp/iosApp/ContentView.swift
import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel = LoginViewModelWrapper()
    
    var body: some View {
        NavigationView {
            if viewModel.state.isLoggedIn {
                MainTabView()
            } else {
                LoginView(viewModel: viewModel)
            }
        }
    }
}

// iosApp/iosApp/LoginViewModelWrapper.swift
class LoginViewModelWrapper: ObservableObject {
    private let viewModel: LoginViewModel
    @Published var state = LoginState()
    
    init() {
        viewModel = LoginViewModel(loginUseCase: DIContainer.shared.loginUseCase)
        
        viewModel.state.collect { [weak self] state in
            DispatchQueue.main.async {
                self?.state = state
            }
        }
    }
    
    func login(email: String, password: String) {
        viewModel.login(email: email, password: password)
    }
}
```

#### 5.2 依赖注入配置
```kotlin
// shared/src/commonMain/kotlin/di/DIContainer.kt
class DIContainer {
    private val apiClient = ApiClient()
    private val preferencesManager = PreferencesManager()
    
    val userRepository: UserRepository = UserRepositoryImpl(
        apiClient = apiClient,
        preferencesManager = preferencesManager
    )
    
    val loginUseCase = LoginUseCase(
        userRepository = userRepository,
        preferencesManager = preferencesManager
    )
}
```

## 🔧 技术栈对照表

### 网络层迁移
| Android (当前) | KMP (目标) | 迁移说明 |
|----------------|------------|----------|
| Retrofit + OkHttp | Ktor Client | API接口定义保持一致，底层实现替换 |
| Gson | kotlinx.serialization | 数据模型添加@Serializable注解 |
| Interceptor | Ktor Features | 拦截器逻辑保持，实现方式调整 |

### 数据存储迁移
| Android (当前) | KMP (目标) | 迁移说明 |
|----------------|------------|----------|
| Room | SQLDelight | SQL语法基本一致，代码生成方式不同 |
| DataStore | MultiplatformSettings | API相似，跨平台支持 |
| SharedPreferences | expect/actual | 平台特定实现，接口统一 |

### 依赖注入迁移
| Android (当前) | KMP (目标) | 迁移说明 |
|----------------|------------|----------|
| Hilt | Koin | 注解 → DSL，依赖图保持一致 |
| @Inject Constructor | by inject() | 构造函数注入 → 属性注入 |
| @Module | module { } | 模块定义语法调整 |

## 📈 迁移收益评估

### 开发效率
- **代码复用率**：预期达到70-80%
- **功能开发**：新功能一次开发，双平台受益
- **维护成本**：业务逻辑统一维护，降低50%维护工作量

### 技术收益
- **类型安全**：Kotlin类型系统跨平台一致性
- **性能优化**：原生性能，无中间层损耗
- **生态整合**：充分利用各平台原生生态

### 商业价值
- **市场覆盖**：同时支持Android和iOS用户
- **开发成本**：相比独立开发两个应用，节省40-50%开发成本
- **上市时间**：新功能双平台同步发布

## ⚠️ 迁移风险与应对

### 技术风险
1. **学习曲线**：团队需要学习KMP相关技术
   - **应对**：渐进式迁移，逐步积累经验
   
2. **生态成熟度**：部分第三方库可能不支持KMP
   - **应对**：优先选择KMP友好的库，必要时自行封装

3. **调试复杂度**：跨平台调试相对复杂
   - **应对**：完善日志系统，建立调试最佳实践

### 项目风险
1. **开发进度**：迁移期间可能影响功能开发速度
   - **应对**：分阶段迁移，保证Android开发不受影响
   
2. **团队配合**：需要Android和iOS开发者密切协作
   - **应对**：建立跨平台开发规范和协作流程

## 📚 学习资源推荐

### 官方文档
- [Kotlin Multiplatform 官方文档](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform 文档](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Ktor Client 文档](https://ktor.io/docs/client.html)

### 实践案例
- [KaMPKit](https://github.com/touchlab/KaMPKit) - KMP最佳实践模板
- [JetBrains Fleet](https://blog.jetbrains.com/kotlin/2021/10/fleet-kotlin-multiplatform/) - 大型KMP应用案例

### 技术博客
- [Touchlab KMP 博客系列](https://touchlab.co/kotlin-multiplatform)
- [JetBrains KMP 技术分享](https://blog.jetbrains.com/kotlin/category/multiplatform/)

## 🎯 总结

MateLink当前的模块化架构已经为KMP迁移奠定了良好基础。通过渐进式迁移策略，可以在保持Android开发效率的同时，逐步实现跨平台能力。关键成功因素：

1. **架构设计**：坚持接口化、分层架构原则
2. **渐进迁移**：分阶段实施，降低风险
3. **团队协作**：建立跨平台开发规范
4. **持续学习**：跟进KMP生态发展

**预期时间线**：完整迁移周期约3-4个月，其中前2个月可以与Android功能开发并行进行，后期专注于iOS平台适配和优化。

通过这种方式，MateLink将具备强大的跨平台能力，为未来的市场扩展和技术发展奠定坚实基础。
