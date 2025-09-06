# MateLink 友联 Android 项目开发流程文档（模块化架构版）

## 一、项目初始化与环境搭建

### 1. 开发环境配置
- **基础工具**：Android Studio 2023.1.1+、Git
- **技术栈**：
    - 语言：Kotlin 1.9.0+
    - UI框架：Jetpack Compose 1.5.0+
    - 架构组件：ViewModel、StateFlow、Hilt
    - 网络：Retrofit 2.9.0+、OkHttp 4.11.0+
    - 本地存储：Room 2.5.2+、DataStore
- **编译配置**：
    - Gradle 8.7
    - 编译SDK：API 34
    - 最小支持SDK：API 24
    - 目标SDK：API 34

### 2. 优化后的项目结构搭建

基于现代Android开发最佳实践，我们采用更加清晰和可维护的模块化架构：

```
MateLink/
├── app/                          # 主模块（壳工程）- 只负责应用启动和导航
│   ├── src/main/java/com/xichen/matelink/
│   │   ├── MainActivity.kt       # 应用入口Activity
│   │   ├── App.kt                # 应用初始化类
│   │   ├── AppNavGraph.kt        # 应用导航图
│   │   └── di/                   # 应用级依赖注入
│   │       └── AppModule.kt
│   └── src/main/res/             # 全局资源文件

├── core/                         # 核心模块 - 通用功能和工具
│   ├── common/                   # 通用工具类
│   │   ├── src/main/java/com/xichen/matelink/core/common/
│   │   │   ├── utils/            # 工具类
│   │   │   │   ├── DateUtils.kt
│   │   │   │   ├── StringUtils.kt
│   │   │   │   └── FileUtils.kt
│   │   │   ├── extensions/       # Kotlin扩展函数
│   │   │   └── constants/        # 常量定义
│   ├── network/                  # 网络层
│   │   ├── src/main/java/com/xichen/matelink/core/network/
│   │   │   ├── api/              # API接口定义
│   │   │   ├── model/            # 网络模型
│   │   │   ├── interceptor/      # 网络拦截器
│   │   │   └── ApiClient.kt      # Retrofit客户端
│   ├── database/                 # 数据库层
│   │   ├── src/main/java/com/xichen/matelink/core/database/
│   │   │   ├── dao/              # 数据访问对象
│   │   │   ├── entity/           # 数据库实体
│   │   │   └── AppDatabase.kt    # 数据库配置
│   ├── ui/                       # UI组件库
│   │   ├── src/main/java/com/xichen/matelink/core/ui/
│   │   │   ├── component/        # 通用UI组件
│   │   │   │   ├── Button.kt
│   │   │   │   ├── TextField.kt
│   │   │   │   └── LoadingIndicator.kt
│   │   │   ├── theme/            # 主题配置
│   │   │   └── composable/       # 可组合函数
│   ├── data/                     # 数据层
│   │   ├── src/main/java/com/xichen/matelink/core/data/
│   │   │   ├── repository/       # 数据仓库
│   │   │   ├── model/            # 数据模型
│   │   │   └── PreferencesManager.kt
│   └── di/                       # 依赖注入模块
│       ├── src/main/java/com/xichen/matelink/core/di/
│       │   ├── NetworkModule.kt
│       │   ├── DatabaseModule.kt
│       │   └── RepositoryModule.kt

├── feature/                      # 功能模块 - 按业务功能拆分
│   ├── auth/                     # 认证模块（登录、注册等）
│   │   ├── src/main/java/com/xichen/matelink/feature/auth/
│   │   │   ├── ui/               # UI层
│   │   │   │   ├── login/        # 登录相关界面
│   │   │   │   └── register/     # 注册相关界面
│   │   │   ├── domain/           # 领域层
│   │   │   │   ├── model/        # 领域模型
│   │   │   │   ├── repository/   # 领域仓库接口
│   │   │   │   └── usecase/      # 用例
│   │   │   └── data/             # 数据层实现
│   │   │       ├── repository/   # 仓库实现
│   │   │       ├── api/          # API接口
│   │   │       └── local/        # 本地数据源
│   │   └── di/                   # 模块依赖注入
│   │       └── AuthModule.kt
│
│   ├── space/                    # 空间模块
│   │   ├── src/main/java/com/xichen/matelink/feature/space/
│   │   │   ├── ui/               # 空间相关界面
│   │   │   │   ├── list/         # 空间列表
│   │   │   │   ├── detail/       # 空间详情
│   │   │   │   └── create/       # 创建空间
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   └── usecase/
│   │   │   └── data/
│   │   │       ├── repository/
│   │   │       ├── api/
│   │   │       └── local/
│   │   └── di/
│
│   ├── chat/                     # 聊天模块
│   │   ├── src/main/java/com/xichen/matelink/feature/chat/
│   │   │   ├── ui/
│   │   │   │   ├── conversation/ # 会话列表
│   │   │   │   └── message/      # 消息界面
│   │   │   ├── domain/
│   │   │   ├── data/
│   │   │   └── di/
│
│   ├── moment/                   # 朋友圈模块
│   │   ├── src/main/java/com/xichen/matelink/feature/moment/
│   │   │   ├── ui/
│   │   │   │   ├── feed/         # 动态流
│   │   │   │   └── publish/      # 发布动态
│   │   │   ├── domain/
│   │   │   ├── data/
│   │   │   └── di/
│
│   └── profile/                  # 个人资料模块
│       ├── src/main/java/com/xichen/matelink/feature/profile/
│       │   ├── ui/
│       │   ├── domain/
│       │   ├── data/
│       │   └── di/

├── shared/                       # 共享模块 - 跨功能共享的代码
│   ├── src/main/java/com/xichen/matelink/shared/
│   │   ├── model/                # 共享数据模型
│   │   ├── utils/                # 共享工具类
│   │   └── component/            # 共享UI组件

├── build-logic/                  # 构建逻辑模块
│   ├── src/main/kotlin/
│   │   ├── AndroidCompose.kt     # Compose配置
│   │   ├── AndroidLibrary.kt     # 库模块配置
│   │   └── AndroidApplication.kt # 应用模块配置

├── gradle/                       # Gradle配置
│   ├── libs.versions.toml        # 版本管理
│   └── wrapper/

├── config/                       # 配置目录
│   ├── detekt/                   # 代码质量检查配置
│   ├── ktlint/                   # Kotlin代码风格配置
│   └── jacoco/                   # 测试覆盖率配置

└── docs/                         # 项目文档
    ├── architecture/             # 架构文档
    ├── api/                      # API文档
    └── guides/                   # 开发指南
```

### 3. 架构优化说明

#### 3.1 Clean Architecture分层
```
┌─────────────────────────────────────┐
│           Presentation Layer       │  ← UI层（Compose）
│           (feature/*/ui)           │
├─────────────────────────────────────┤
│           Domain Layer             │  ← 领域层（UseCase）
│           (feature/*/domain)       │
├─────────────────────────────────────┤
│           Data Layer               │  ← 数据层（Repository）
│           (feature/*/data)         │
├─────────────────────────────────────┤
│           Core Layer               │  ← 核心层（网络、数据库等）
│           (core/*)                 │
└─────────────────────────────────────┘
```

#### 3.2 模块职责划分
- **app**: 应用入口，负责导航和全局配置
- **core**: 提供基础设施服务（网络、数据库、通用UI组件等）
- **feature**: 业务功能模块，按Clean Architecture分层
- **shared**: 跨模块共享的代码和组件
- **build-logic**: 构建逻辑复用

#### 3.3 优势对比

| 方面 | 原架构 | 优化架构 |
|------|--------|----------|
| 可维护性 | 模块耦合度高 | 高内聚，低耦合 |
| 可测试性 | 难以单元测试 | 各层独立测试 |
| 可扩展性 | 功能增加困难 | 易于功能扩展 |
| 构建速度 | 全量编译 | 增量编译支持 |
| 团队协作 | 冲突频发 | 模块独立开发 |

### 4. 实现步骤

#### 4.1 第一阶段：创建核心模块
```bash
# 创建核心模块目录结构
mkdir -p core/common/src/main/java/com/xichen/matelink/core/common
mkdir -p core/network/src/main/java/com/xichen/matelink/core/network
mkdir -p core/database/src/main/java/com/xichen/matelink/core/database
mkdir -p core/ui/src/main/java/com/xichen/matelink/core/ui
mkdir -p core/data/src/main/java/com/xichen/matelink/core/data
```

#### 4.2 第二阶段：迁移现有代码
- 将工具类迁移到 `core/common`
- 将网络相关代码迁移到 `core/network`
- 将数据库相关代码迁移到 `core/database`
- 将UI组件迁移到 `core/ui`

#### 4.3 第三阶段：创建功能模块
```bash
# 创建功能模块
mkdir -p feature/auth/src/main/java/com/xichen/matelink/feature/auth
mkdir -p feature/space/src/main/java/com/xichen/matelink/feature/space
mkdir -p feature/chat/src/main/java/com/xichen/matelink/feature/chat
mkdir -p feature/moment/src/main/java/com/xichen/matelink/feature/moment
```

#### 4.4 第四阶段：配置依赖关系
```kotlin
// settings.gradle.kts
include(
    ":app",
    ":core:common",
    ":core:network",
    ":core:database",
    ":core:ui",
    ":core:data",
    ":feature:auth",
    ":feature:space",
    ":feature:chat",
    ":feature:moment"
)
```

### 3. 基础模块初始化
1. 创建`core`模块，实现：
    - 网络请求基础配置（`ApiClient`）
    - 统一数据结果封装（`Result`）
    - 通用工具类（日志、Toast、日期处理）
    - 基础UI组件（按钮、输入框、加载状态）
2. 配置`app`模块作为壳工程，依赖所有功能模块
3. 集成Hilt实现依赖注入，配置`Application`类

### 4. 项目配置与依赖管理

#### 4.1 版本管理配置
```kotlin
// gradle/libs.versions.toml
[versions]
kotlin = "1.9.0"
compose = "1.5.0"
hilt = "2.48"
retrofit = "2.9.0"
room = "2.5.2"
coil = "2.4.0"
navigation = "2.7.0"

[libraries]
# Core Android
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "androidx-core" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "androidx-lifecycle" }

# Compose
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }

# Navigation
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
androidx-hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.0.0" }

# Network
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version = "4.11.0" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version = "4.11.0" }

# Database
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }

# Image Loading
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

# Testing
junit = { group = "junit", name = "junit", version = "4.13.2" }
androidx-test-ext-junit = { group = "androidx.test.ext", name = "junit", version = "1.1.5" }
androidx-test-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version = "3.5.1" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

#### 4.2 模块依赖配置
```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.xichen.matelink"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.xichen.matelink"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("String", "BASE_URL", "\"https://dev-api.matelink.com/\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://api.matelink.com/\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "false")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core modules
    implementation(project(":core"))
    implementation(project(":feature:login"))
    implementation(project(":feature:space"))
    implementation(project(":feature:chat"))
    implementation(project(":feature:moment"))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
```

#### 4.3 核心模块配置
```kotlin
// core/build.gradle.kts
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.xichen.matelink.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // Image Loading
    implementation(libs.coil.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
```

#### 4.4 功能模块配置示例
```kotlin
// feature/login/build.gradle.kts
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.xichen.matelink.feature.login"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}

dependencies {
    // Core module
    implementation(project(":core"))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
```

#### 4.5 项目级配置
```kotlin
// build.gradle.kts (Project level)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
```

#### 4.6 混淆配置
```proguard
# proguard-rules.pro
# Add project specific ProGuard rules here.

# Keep data classes
-keep class com.xichen.matelink.**.data.** { *; }

# Keep Retrofit interfaces
-keep interface com.xichen.matelink.**.api.** { *; }

# Keep Room entities
-keep class com.xichen.matelink.**.database.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
```

## 二、核心通用模块开发

### 1. 网络通信层
- 实现`ApiClient`：配置BaseUrl、超时时间、日志拦截器
- 封装网络请求工具类：自动处理token添加、错误统一处理
- 定义通用`Result`类：处理成功/失败/加载状态

```kotlin
// core/network/ApiClient.kt
object ApiClient {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.matelink.com/")
        .client(OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

// core/network/Result.kt
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int = -1) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

### 2. 本地存储层
- 初始化Room数据库：创建数据库实例和基础DAO接口
- 实现DataStore：存储用户配置、登录状态等
- 封装安全存储工具：处理敏感信息（token、用户凭证）

### 3. 通用UI组件
- 实现`BaseButton`：统一按钮样式和点击效果
- 实现`InputTextField`：带验证功能的输入框
- 实现状态组件：`LoadingView`、`ErrorView`、`EmptyView`
- 配置主题：统一颜色、字体、形状

## 三、业务模块开发（按优先级排序）

### 1. 登录模块（feature/login）
#### 开发步骤：
1. 定义数据模型：`User`（用户信息）、`LoginRequest`（请求参数）
2. 创建登录接口：`LoginApi`（登录、注册、验证码接口）
3. 实现`LoginViewModel`：
    - 处理登录逻辑（验证输入、调用接口、保存登录状态）
    - 管理UI状态（加载中、登录成功、登录失败）
4. 开发登录界面（`LoginScreen`）：
    - 账号密码输入框
    - 登录按钮（带加载状态）
    - 注册/忘记密码入口
    - 第三方登录选项（可选）
5. 添加验证逻辑：手机号/邮箱格式校验、密码强度检测
6. 实现自动登录：登录成功后保存token，启动时验证

#### 关键代码示例：
```kotlin
// LoginViewModel.kt
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginApi: LoginApi,
    private val prefs: Prefs
) : ViewModel() {
    private val _state = MutableStateFlow<Result<User>>(Result.Loading)
    val state: StateFlow<Result<User>> = _state.asStateFlow()

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _state.value = Result.Loading
            val result = try {
                val response = loginApi.login(LoginRequest(phone, password))
                if (response.success) {
                    prefs.saveToken(response.token)
                    Result.Success(response.user)
                } else {
                    Result.Error(response.message)
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "登录失败，请重试")
            }
            _state.value = result
        }
    }
}
```

### 2. 主框架与空间模块（feature/space）
#### 开发步骤：
1. 实现主界面框架：
    - 底部导航栏（首页、聊天、朋友圈、我的）
    - 导航管理（使用Compose Navigation）
2. 开发空间列表功能：
    - 定义`Space`数据模型
    - 实现`SpaceApi`（获取空间列表、创建空间接口）
    - 开发`SpaceListScreen`：展示用户加入的空间
3. 实现空间创建功能：
    - 创建空间表单（名称、描述、类型选择）
    - 空间创建逻辑与权限设置
    - 面对面创建：使用二维码或NFC技术实现面对面空间创建
4. 开发空间详情页：
    - 空间信息展示
    - 成员管理入口
    - 空间动态预览
    - 空间设置和权限管理

#### 关键代码示例：
```kotlin
// SpaceViewModel.kt
@HiltViewModel
class SpaceViewModel @Inject constructor(
    private val spaceApi: SpaceApi,
    private val qrCodeGenerator: QRCodeGenerator
) : ViewModel() {
    
    private val _spaces = MutableStateFlow<List<Space>>(emptyList())
    val spaces: StateFlow<List<Space>> = _spaces.asStateFlow()
    
    fun createSpace(name: String, description: String, type: SpaceType) {
        viewModelScope.launch {
            try {
                val space = spaceApi.createSpace(CreateSpaceRequest(name, description, type))
                _spaces.value = _spaces.value + space
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }
    
    fun generateJoinQRCode(spaceId: String): Bitmap {
        val joinCode = spaceApi.generateJoinCode(spaceId)
        return qrCodeGenerator.generateQRCode(joinCode)
    }
}

// Space.kt
data class Space(
    val id: String,
    val name: String,
    val description: String,
    val type: SpaceType,
    val memberCount: Int,
    val isAdmin: Boolean,
    val joinCode: String? = null
)

enum class SpaceType {
    PERSONAL,    // 个人空间
    GROUP,       // 群组空间
    PUBLIC       // 公共空间
}
```

### 3. 聊天模块（feature/chat）
#### 开发步骤：
1. 设计消息数据模型：`Message`（内容、类型、发送者、时间）
2. 实现聊天接口：`ChatApi`（发送消息、获取历史记录）
3. 本地存储设计：
    - `ChatDao`：存储聊天记录
    - 实现消息缓存与同步策略
4. 开发聊天界面：
    - 消息列表（使用LazyColumn）
    - 消息气泡（区分自己和他人消息）
    - 输入框与发送按钮
    - 支持多种消息类型（文字、图片、表情）
5. 实现聊天逻辑：
    - 消息发送与状态更新（发送中、已发送、已读）
    - 历史记录加载与分页
    - 消息撤回功能（2分钟内）

#### 关键代码示例：
```kotlin
// Message.kt
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val content: String,
    val type: MessageType,
    val senderId: String,
    val receiverId: String? = null, // null表示群聊
    val spaceId: String,
    val timestamp: Long,
    val status: MessageStatus = MessageStatus.SENDING,
    val replyToId: String? = null
)

enum class MessageType {
    TEXT, IMAGE, VOICE, VIDEO, FILE, EMOJI
}

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ, FAILED
}

// ChatViewModel.kt
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatApi: ChatApi,
    private val messageDao: MessageDao,
    private val websocketManager: WebSocketManager
) : ViewModel() {
    
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    
    fun sendMessage(content: String, type: MessageType, spaceId: String) {
        val message = Message(
            id = UUID.randomUUID().toString(),
            content = content,
            type = type,
            senderId = getCurrentUserId(),
            spaceId = spaceId,
            timestamp = System.currentTimeMillis()
        )
        
        viewModelScope.launch {
            // 先保存到本地
            messageDao.insertMessage(message)
            _messages.value = _messages.value + message
            
            try {
                // 发送到服务器
                val result = chatApi.sendMessage(SendMessageRequest(message))
                if (result.success) {
                    // 更新消息状态
                    val updatedMessage = message.copy(status = MessageStatus.SENT)
                    messageDao.updateMessage(updatedMessage)
                } else {
                    // 发送失败
                    val failedMessage = message.copy(status = MessageStatus.FAILED)
                    messageDao.updateMessage(failedMessage)
                }
            } catch (e: Exception) {
                val failedMessage = message.copy(status = MessageStatus.FAILED)
                messageDao.updateMessage(failedMessage)
            }
        }
    }
    
    fun recallMessage(messageId: String) {
        viewModelScope.launch {
            val message = messageDao.getMessageById(messageId)
            if (message != null && canRecallMessage(message)) {
                chatApi.recallMessage(messageId)
                messageDao.deleteMessage(messageId)
                _messages.value = _messages.value.filter { it.id != messageId }
            }
        }
    }
    
    private fun canRecallMessage(message: Message): Boolean {
        val timeDiff = System.currentTimeMillis() - message.timestamp
        return timeDiff <= 2 * 60 * 1000 // 2分钟内
    }
}

// ChatScreen.kt
@Composable
fun ChatScreen(
    spaceId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 消息列表
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(
                    message = message,
                    onRecallClick = { viewModel.recallMessage(message.id) }
                )
            }
        }
        
        // 输入框
        MessageInput(
            onSendMessage = { content, type ->
                viewModel.sendMessage(content, type, spaceId)
            }
        )
    }
}
```

### 4. 朋友圈模块（feature/moment）
#### 开发步骤：
1. 定义动态数据模型：`Moment`（内容、图片、发布者、时间、互动信息）
2. 实现朋友圈接口：`MomentApi`（发布、获取列表、点赞、评论）
3. 开发朋友圈列表页：
    - 瀑布流布局展示动态
    - 下拉刷新、上拉加载更多
4. 实现发布功能：
    - 发布表单（文字输入、图片选择）
    - 发布预览与发布逻辑
5. 添加互动功能：
    - 点赞/取消点赞
    - 评论列表与发布评论
    - 动态分享

#### 关键代码示例：
```kotlin
// Moment.kt
@Entity(tableName = "moments")
data class Moment(
    @PrimaryKey val id: String,
    val content: String,
    val images: List<String> = emptyList(),
    val authorId: String,
    val authorName: String,
    val authorAvatar: String,
    val spaceId: String,
    val timestamp: Long,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false,
    val privacy: MomentPrivacy = MomentPrivacy.SPACE_FRIENDS
)

enum class MomentPrivacy {
    PUBLIC,         // 公开
    SPACE_FRIENDS,  // 空间好友可见
    PRIVATE         // 仅自己可见
}

// MomentViewModel.kt
@HiltViewModel
class MomentViewModel @Inject constructor(
    private val momentApi: MomentApi,
    private val momentDao: MomentDao
) : ViewModel() {
    
    private val _moments = MutableStateFlow<List<Moment>>(emptyList())
    val moments: StateFlow<List<Moment>> = _moments.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadMoments(spaceId: String, refresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val moments = if (refresh) {
                    momentApi.getMoments(spaceId, 0, 20)
                } else {
                    val localMoments = momentDao.getMoments(spaceId)
                    if (localMoments.isNotEmpty()) {
                        localMoments
                    } else {
                        momentApi.getMoments(spaceId, 0, 20)
                    }
                }
                
                momentDao.insertMoments(moments)
                _moments.value = moments
            } catch (e: Exception) {
                // 处理错误
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun publishMoment(content: String, images: List<String>, spaceId: String) {
        viewModelScope.launch {
            try {
                val moment = momentApi.publishMoment(
                    PublishMomentRequest(content, images, spaceId)
                )
                
                _moments.value = listOf(moment) + _moments.value
                momentDao.insertMoment(moment)
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }
    
    fun toggleLike(momentId: String) {
        viewModelScope.launch {
            try {
                val result = momentApi.toggleLike(momentId)
                val updatedMoments = _moments.value.map { moment ->
                    if (moment.id == momentId) {
                        moment.copy(
                            isLiked = result.isLiked,
                            likeCount = result.likeCount
                        )
                    } else {
                        moment
                    }
                }
                _moments.value = updatedMoments
                momentDao.updateMoment(updatedMoments.find { it.id == momentId }!!)
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }
}

// MomentScreen.kt
@Composable
fun MomentScreen(
    spaceId: String,
    viewModel: MomentViewModel = hiltViewModel()
) {
    val moments by viewModel.moments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(spaceId) {
        viewModel.loadMoments(spaceId, refresh = true)
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 发布按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "朋友圈",
                style = MaterialTheme.typography.h5
            )
            IconButton(onClick = { /* 打开发布页面 */ }) {
                Icon(Icons.Default.Add, contentDescription = "发布动态")
            }
        }
        
        // 动态列表
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(moments) { moment ->
                MomentCard(
                    moment = moment,
                    onLikeClick = { viewModel.toggleLike(moment.id) },
                    onCommentClick = { /* 打开评论 */ }
                )
            }
            
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
```

## 四、公共功能开发

### 1. 多语言支持
- 在`core`模块配置多语言资源文件
- 实现`LanguageManager`：管理语言切换
- 在设置界面添加语言选择功能
- 测试不同语言下的UI适配

### 2. 主题与适配
- 实现深色/浅色模式切换
- 适配不同屏幕尺寸（手机、平板）
- 处理屏幕旋转等配置变化
- 优化字体大小适配

### 3. 通知功能
- 集成推送服务（Firebase或厂商推送）
- 实现消息通知、互动通知
- 配置通知渠道（Android 8.0+）
- 通知点击跳转对应页面

## 五、测试与优化

### 1. 测试策略
- **单元测试**：
    - 测试ViewModel逻辑（使用JUnit、Mockito）
    - 测试工具类与业务逻辑
- **UI测试**：
    - 关键流程测试（登录、发送消息）
    - 使用Espresso测试界面交互
- **兼容性测试**：
    - 在主流机型上测试（覆盖不同品牌和系统版本）
    - 测试网络异常场景（弱网、断网）

### 2. 性能优化
- 图片加载优化：使用Coil加载图片，实现缓存和压缩
- 列表优化：实现RecyclerView回收复用，避免过度绘制
- 启动优化：减少初始化操作，延迟加载非必要组件
- 内存优化：避免内存泄漏（正确管理生命周期）

### 3. 安全优化
- 敏感数据加密（如用户密码、token）
- 网络请求加密（HTTPS）
- 输入验证（防止注入攻击）
- 权限管理（按需申请权限）

## 六、发布准备

### 1. 应用配置
- 生成签名密钥与配置
- 配置应用名称、图标、版本信息
- 准备应用描述、截图等上架材料

### 2. 构建与打包
- 配置混淆规则（proguard-rules.pro）
- 生成正式环境APK/AAB
- 测试正式包功能完整性

### 3. 发布渠道
- Google Play商店
- 国内主流应用市场（华为、小米、OPPO、vivo等）
- 企业内部分发渠道（如适用）

## 七、迭代计划

### 第一阶段：核心功能
- 登录/注册功能
- 空间管理（创建、加入、列表）
- 基础聊天功能（文字消息）

### 第二阶段：功能完善
- 朋友圈发布与浏览
- 多媒体消息（图片、语音）
- 消息通知

### 第三阶段：体验优化
- 性能优化
- 多语言支持
- 深色模式

### 第四阶段：高级功能
- 视频聊天
- 空间互动游戏
- 数据分析与个性化推荐

## 八、Git工作流与代码管理

### 1. Git分支策略
- **主分支**：
    - `main`：生产环境代码，只接受来自`develop`的合并
    - `develop`：开发环境代码，功能开发的主分支
- **功能分支**：
    - `feature/模块名-功能描述`：如`feature/login-biometric`
    - `bugfix/问题描述`：如`bugfix/chat-message-crash`
    - `hotfix/紧急修复描述`：如`hotfix/login-token-expire`
- **发布分支**：
    - `release/版本号`：如`release/v1.0.0`，用于版本发布准备

### 2. 提交规范
使用约定式提交（Conventional Commits）：
```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

类型说明：
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

示例：
```bash
feat(login): 添加指纹登录功能
fix(chat): 修复消息发送失败问题
docs(readme): 更新项目安装说明
```

### 3. 代码审查流程
1. **Pull Request创建**：
    - 功能开发完成后创建PR
    - 填写详细的PR描述和测试说明
    - 关联相关Issue
2. **代码审查**：
    - 至少需要1名团队成员审查
    - 检查代码质量、性能、安全性
    - 确保测试覆盖率达标
3. **合并规则**：
    - 所有CI检查通过
    - 代码审查通过
    - 无冲突代码

## 九、CI/CD持续集成

### 1. GitHub Actions配置
```yaml
# .github/workflows/android.yml
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
    
    - name: Run tests
      run: ./gradlew test
    
    - name: Run lint
      run: ./gradlew lint
    
    - name: Build APK
      run: ./gradlew assembleDebug
```

### 2. 自动化测试
- **单元测试**：每次提交自动运行
- **集成测试**：PR合并前运行
- **UI测试**：发布前运行完整测试套件

### 3. 自动化部署
- **开发环境**：develop分支自动部署到测试环境
- **生产环境**：main分支手动触发部署
- **版本发布**：自动生成APK/AAB并上传到分发平台

## 十、错误处理与监控

### 1. 错误处理策略
```kotlin
// core/network/ErrorHandler.kt
object ErrorHandler {
    fun handleError(throwable: Throwable): String {
        return when (throwable) {
            is SocketTimeoutException -> "网络连接超时，请检查网络设置"
            is UnknownHostException -> "网络连接失败，请检查网络"
            is HttpException -> when (throwable.code()) {
                401 -> "登录已过期，请重新登录"
                403 -> "权限不足"
                404 -> "请求的资源不存在"
                500 -> "服务器内部错误"
                else -> "网络请求失败"
            }
            is JsonSyntaxException -> "数据解析错误"
            else -> "未知错误：${throwable.message}"
        }
    }
}
```

### 2. 崩溃监控
集成Firebase Crashlytics：
```kotlin
// App.kt
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        
        // 设置崩溃报告
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
}
```

### 3. 性能监控
- **启动时间监控**：记录应用启动各阶段耗时
- **内存使用监控**：监控内存泄漏和峰值使用
- **网络性能监控**：记录API响应时间和成功率
- **UI性能监控**：监控界面渲染性能

## 十一、安全开发指南

### 1. 数据安全
```kotlin
// core/security/EncryptionUtil.kt
object EncryptionUtil {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val KEY_LENGTH = 256
    
    fun encrypt(data: String, key: SecretKey): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        val iv = cipher.iv
        return Base64.encodeToString(encryptedBytes + iv, Base64.DEFAULT)
    }
    
    fun decrypt(encryptedData: String, key: SecretKey): String {
        val decoded = Base64.decode(encryptedData, Base64.DEFAULT)
        val iv = decoded.sliceArray(decoded.size - 12 until decoded.size)
        val encrypted = decoded.sliceArray(0 until decoded.size - 12)
        
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return String(cipher.doFinal(encrypted))
    }
}
```

### 2. 网络安全
- **证书锁定**：防止中间人攻击
- **请求签名**：确保请求完整性
- **敏感数据加密**：token、密码等敏感信息加密存储

### 3. 权限管理
```kotlin
// core/permission/PermissionManager.kt
class PermissionManager(private val activity: ComponentActivity) {
    fun requestPermission(
        permission: String,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        when {
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED -> {
                onGranted()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                showPermissionRationale(permission, onGranted, onDenied)
            }
            else -> {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE)
            }
        }
    }
}
```

## 十二、测试策略详解

### 1. 单元测试
```kotlin
// test/LoginViewModelTest.kt
@ExtendWith(MockKExtension::class)
class LoginViewModelTest {
    
    @MockK
    private lateinit var loginApi: LoginApi
    
    @MockK
    private lateinit var prefs: Prefs
    
    private lateinit var viewModel: LoginViewModel
    
    @BeforeEach
    fun setup() {
        viewModel = LoginViewModel(loginApi, prefs)
    }
    
    @Test
    fun `login with valid credentials should return success`() = runTest {
        // Given
        val phone = "13800138000"
        val password = "password123"
        val expectedUser = User(id = 1, phone = phone)
        val response = LoginResponse(success = true, token = "token", user = expectedUser)
        
        coEvery { loginApi.login(any()) } returns response
        
        // When
        viewModel.login(phone, password)
        
        // Then
        val state = viewModel.state.value
        assertTrue(state is Result.Success)
        assertEquals(expectedUser, (state as Result.Success).data)
    }
}
```

### 2. 集成测试
```kotlin
// androidTest/DatabaseTest.kt
@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    
    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = database.userDao()
    }
    
    @Test
    fun insertAndGetUser() = runTest {
        val user = User(id = 1, phone = "13800138000", name = "Test User")
        userDao.insertUser(user)
        
        val retrievedUser = userDao.getUserById(1)
        assertEquals(user, retrievedUser)
    }
}
```

### 3. UI测试
```kotlin
// androidTest/LoginScreenTest.kt
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun loginScreen_displaysCorrectly() {
        composeTestRule.setContent {
            LoginScreen(
                onLoginClick = {},
                onRegisterClick = {},
                isLoading = false
            )
        }
        
        composeTestRule.onNodeWithText("登录").assertIsDisplayed()
        composeTestRule.onNodeWithText("注册").assertIsDisplayed()
    }
    
    @Test
    fun login_withValidInput_callsOnLoginClick() {
        var loginCalled = false
        
        composeTestRule.setContent {
            LoginScreen(
                onLoginClick = { loginCalled = true },
                onRegisterClick = {},
                isLoading = false
            )
        }
        
        composeTestRule.onNodeWithText("手机号").performTextInput("13800138000")
        composeTestRule.onNodeWithText("密码").performTextInput("password123")
        composeTestRule.onNodeWithText("登录").performClick()
        
        assertTrue(loginCalled)
    }
}
```

## 十三、部署与发布流程

### 1. 环境配置
```kotlin
// build.gradle.kts
android {
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://dev-api.matelink.com/\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://api.matelink.com/\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "false")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

### 2. 发布流程
1. **版本准备**：
    - 更新版本号和版本名称
    - 生成发布说明
    - 创建release分支
2. **构建验证**：
    - 运行完整测试套件
    - 进行安全扫描
    - 性能测试
3. **发布部署**：
    - 构建正式版本APK/AAB
    - 上传到应用商店
    - 更新生产环境配置

### 3. 回滚策略
- **应用层回滚**：通过应用商店快速回滚到上一版本
- **服务层回滚**：API服务快速切换到备用版本
- **数据回滚**：数据库备份和恢复机制

## 十四、监控与日志

### 1. 日志管理
```kotlin
// core/util/LogUtil.kt
object LogUtil {
    private const val TAG = "MateLink"
    
    fun d(message: String, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
    
    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        Log.e(tag, message, throwable)
        // 发送到远程日志服务
        sendToRemoteLog(LogLevel.ERROR, message, throwable)
    }
    
    private fun sendToRemoteLog(level: LogLevel, message: String, throwable: Throwable?) {
        // 实现远程日志发送逻辑
    }
}
```

### 2. 性能监控
- **启动性能**：记录应用启动各阶段耗时
- **内存监控**：监控内存使用和泄漏
- **网络监控**：API响应时间和成功率统计
- **崩溃监控**：实时崩溃报告和分析

### 3. 用户行为分析
```kotlin
// core/analytics/AnalyticsManager.kt
object AnalyticsManager {
    fun trackEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        // 发送到分析平台（如Firebase Analytics）
        FirebaseAnalytics.getInstance(context).logEvent(eventName, Bundle().apply {
            parameters.forEach { (key, value) ->
                putString(key, value.toString())
            }
        })
    }
    
    fun trackScreenView(screenName: String) {
        trackEvent("screen_view", mapOf("screen_name" to screenName))
    }
}
```

## 十五、常见问题排查

### 1. 构建问题
**问题**：Gradle构建失败
**解决方案**：
- 检查网络连接和代理设置
- 清理Gradle缓存：`./gradlew clean`
- 更新Gradle版本和依赖版本
- 检查Java版本兼容性

**问题**：依赖冲突
**解决方案**：
- 使用`./gradlew app:dependencies`查看依赖树
- 排除冲突的传递依赖
- 使用`resolutionStrategy`强制使用特定版本

### 2. 运行时问题
**问题**：应用崩溃
**排查步骤**：
1. 查看Logcat日志
2. 检查Firebase Crashlytics报告
3. 分析崩溃堆栈信息
4. 复现问题并调试

**问题**：内存泄漏
**排查工具**：
- Android Studio Memory Profiler
- LeakCanary库
- 分析对象引用链

### 3. 网络问题
**问题**：API请求失败
**排查步骤**：
1. 检查网络连接状态
2. 验证API接口地址和参数
3. 查看服务器日志
4. 测试不同网络环境

## 十六、维护规范

### 1. 代码规范
- **命名规范**：
    - 类名使用PascalCase：`LoginViewModel`
    - 方法名使用camelCase：`performLogin`
    - 常量使用UPPER_SNAKE_CASE：`MAX_RETRY_COUNT`
- **注释规范**：
    - 公共API必须添加KDoc注释
    - 复杂逻辑添加行内注释
    - 使用TODO标记待完成功能
- **代码结构**：
    - 每个功能模块保持独立
    - 避免跨模块直接调用
    - 使用依赖注入管理依赖关系

### 2. 版本管理
- **语义化版本**：`主版本.次版本.修订版本`
    - 主版本：不兼容的API修改
    - 次版本：向下兼容的功能性新增
    - 修订版本：向下兼容的问题修正
- **版本日志**：详细记录每个版本的变更内容
- **灰度发布**：重要更新先进行小范围测试

### 3. 问题跟踪
- **Bug分类**：
    - P0：系统崩溃，影响核心功能
    - P1：功能异常，影响用户体验
    - P2：界面问题，不影响功能使用
    - P3：优化建议，非紧急问题
- **处理流程**：
    1. 问题报告和分类
    2. 分配开发人员
    3. 问题修复和测试
    4. 代码审查和合并
    5. 版本发布和验证

### 4. 性能优化
- **定期性能评估**：
    - 启动时间监控
    - 内存使用分析
    - 网络请求优化
    - 界面渲染性能
- **优化策略**：
    - 图片加载优化（Coil、WebP格式）
    - 列表性能优化（LazyColumn、分页加载）
    - 数据库查询优化
    - 网络请求缓存

通过这种完善的开发流程，可以确保MateLink项目的开发质量、维护效率和团队协作效果。每个开发人员都能清楚地了解自己的职责和工作流程，项目的长期发展也能得到有效保障。