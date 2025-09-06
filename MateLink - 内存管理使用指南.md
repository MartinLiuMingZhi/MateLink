# MateLink - 内存管理使用指南

## 概述

MateLink 项目集成了完整的内存管理解决方案，包括内存监控、泄漏检测、优化策略等功能，帮助开发者有效管理应用内存使用，提升应用性能和稳定性。

## 核心组件

### 1. MemoryUtils - 内存工具类

提供基础的内存管理功能：

```kotlin
// 获取内存信息
val memoryInfo = MemoryUtils.getMemoryInfo()

// 检查内存是否充足
val isMemorySufficient = MemoryUtils.isMemorySufficient(threshold = 50 * 1024 * 1024)

// 获取内存使用率
val usagePercent = MemoryUtils.getMemoryUsagePercent()

// 强制垃圾回收
MemoryUtils.forceGarbageCollection()
```

### 2. MemoryLeakDetector - 内存泄漏检测器

自动检测 Activity、Fragment 等组件的内存泄漏：

```kotlin
// 初始化检测器
MemoryLeakDetector.init(application, enabled = true)

// 注册 Activity 引用（在 Activity 的 onCreate 中调用）
MemoryLeakDetector.registerActivity(this)

// 注册 Fragment 引用（在 Fragment 的 onViewCreated 中调用）
MemoryLeakDetector.registerFragment(this)

// 获取当前引用计数
val referenceCount = MemoryLeakDetector.getReferenceCount()

// 获取详细内存信息
val memoryInfo = MemoryLeakDetector.getMemoryInfo()
```

### 3. MemoryOptimizer - 内存优化器

提供自动内存优化策略：

```kotlin
// 初始化优化器
MemoryOptimizer.init(context, enabled = true)

// 手动触发优化
MemoryOptimizer.triggerOptimization(OptimizationLevel.MEDIUM)

// 获取优化建议
val suggestions = MemoryOptimizer.getOptimizationSuggestions()

// 添加自定义优化策略
MemoryOptimizer.addOptimizationStrategy(customStrategy)
```

### 4. CacheManager - 缓存管理器

统一管理各种类型的缓存：

```kotlin
// 获取缓存管理器实例
val cacheManager = CacheManager.getInstance()

// 内存缓存操作
cacheManager.putToMemoryCache("key", data)
val data = cacheManager.getFromMemoryCache<Data>("key")

// 磁盘缓存操作
cacheManager.putToDiskCache("key", data)
val data = cacheManager.getFromDiskCache<Data>("key")

// 清理缓存
cacheManager.clearMemoryCache()
cacheManager.clearDiskCache()
cacheManager.clearAllCache()
```

### 5. StorageManager - 存储管理器

管理不同类型的存储机制：

```kotlin
// 获取存储管理器实例
val storageManager = StorageManager.getInstance(context)

// SharedPreferences 操作
storageManager.putString("key", "value")
val value = storageManager.getString("key", "default")

// DataStore 操作
storageManager.putDataStoreString("key", "value")
val value = storageManager.getDataStoreString("key", "default")

// 加密存储操作
storageManager.putEncryptedString("key", "value")
val value = storageManager.getEncryptedString("key", "default")
```

## 使用场景

### 1. Activity 内存泄漏检测

在 BaseActivity 中自动注册：

```kotlin
abstract class BaseActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 注册内存泄漏检测
        MemoryLeakDetector.registerActivity(this)
    }
}
```

### 2. Fragment 内存泄漏检测

在 BaseFragment 中自动注册：

```kotlin
abstract class BaseFragment : Fragment() {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 注册内存泄漏检测
        MemoryLeakDetector.registerFragment(this)
    }
}
```

### 3. 图片缓存优化

集成 Coil 图片框架的缓存管理：

```kotlin
// 在图片加载时自动管理缓存
@Composable
fun UserAvatar(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = null,
        modifier = modifier
    )
}
```

### 4. 自定义优化策略

创建自定义的内存优化策略：

```kotlin
class CustomOptimizationStrategy : MemoryOptimizationStrategy {
    override val priority = OptimizationPriority.MEDIUM
    
    override fun optimize(level: OptimizationLevel) {
        when (level) {
            OptimizationLevel.LIGHT -> {
                // 轻度优化：清理过期数据
                clearExpiredData()
            }
            OptimizationLevel.MEDIUM -> {
                // 中度优化：清理更多数据
                clearMoreData()
            }
            OptimizationLevel.HEAVY -> {
                // 重度优化：清理所有可清理数据
                clearAllData()
            }
        }
    }
    
    override fun canOptimize(): Boolean = true
    
    private fun clearExpiredData() {
        // 实现清理逻辑
    }
}

// 注册自定义策略
MemoryOptimizer.addOptimizationStrategy(CustomOptimizationStrategy())
```

## 配置选项

### 1. 内存泄漏检测配置

```kotlin
MemoryLeakDetector.configure(
    enabled = true,           // 是否启用检测
    interval = 30L,          // 检查间隔（秒）
    threshold = 100 * 1024 * 1024L  // 内存阈值（字节）
)
```

### 2. 内存优化器配置

```kotlin
MemoryOptimizer.configure(
    enabled = true,                    // 是否启用优化
    interval = 60L,                   // 优化间隔（秒）
    lowThreshold = 50 * 1024 * 1024L, // 低内存阈值
    criticalThreshold = 20 * 1024 * 1024L  // 临界内存阈值
)
```

### 3. 缓存配置

```kotlin
// 内存缓存配置
CacheManager.configureMemoryCache(
    maxSize = 50 * 1024 * 1024L,  // 最大内存缓存大小
    maxEntries = 100              // 最大条目数
)

// 磁盘缓存配置
CacheManager.configureDiskCache(
    maxSize = 200 * 1024 * 1024L, // 最大磁盘缓存大小
    maxEntries = 500              // 最大条目数
)
```

## 监控和调试

### 1. 内存监控界面

使用 `MemoryDemo` 组件进行实时监控：

```kotlin
@Composable
fun MyApp() {
    // 在调试模式下显示内存监控
    if (BuildConfig.DEBUG) {
        MemoryDemo()
    }
}
```

### 2. 日志输出

内存管理工具会输出详细的日志信息：

```
D/MemoryOptimizer: 执行轻度内存优化
D/ImageCacheOptimization: 清理过期图片缓存
W/MemoryLeakDetector: 内存泄漏检测到 Activity: MainActivity
```

### 3. 内存泄漏报告

检测到的内存泄漏会自动保存到文件：

```
/storage/emulated/0/memory_leaks.txt
```

## 最佳实践

### 1. 及时释放资源

```kotlin
class MyActivity : BaseActivity() {
    
    private var heavyObject: HeavyObject? = null
    
    override fun onDestroy() {
        super.onDestroy()
        
        // 及时释放重对象
        heavyObject = null
        
        // 清理缓存
        CacheManager.getInstance().clearMemoryCache()
    }
}
```

### 2. 合理使用缓存

```kotlin
// 对于频繁访问的数据使用内存缓存
val userData = CacheManager.getInstance()
    .getFromMemoryCache<UserData>("user_${userId}") 
    ?: fetchUserDataFromNetwork(userId)

// 对于大文件使用磁盘缓存
val imageData = CacheManager.getInstance()
    .getFromDiskCache<ByteArray>("image_${imageId}")
    ?: downloadImageFromNetwork(imageId)
```

### 3. 监控内存使用

```kotlin
// 在关键操作前后检查内存
val beforeMemory = MemoryLeakDetector.getMemoryInfo()

// 执行重操作
performHeavyOperation()

val afterMemory = MemoryLeakDetector.getMemoryInfo()
val memoryIncrease = afterMemory.usedMemory - beforeMemory.usedMemory

if (memoryIncrease > 10 * 1024 * 1024) { // 10MB
    Log.w("Memory", "内存使用增加过多: ${memoryIncrease / (1024 * 1024)}MB")
}
```

### 4. 自定义优化策略

```kotlin
// 根据应用特点创建优化策略
class AppSpecificOptimization : MemoryOptimizationStrategy {
    override val priority = OptimizationPriority.HIGH
    
    override fun optimize(level: OptimizationLevel) {
        when (level) {
            OptimizationLevel.LIGHT -> {
                // 清理过期的用户数据
                clearExpiredUserData()
            }
            OptimizationLevel.MEDIUM -> {
                // 清理不常用的功能数据
                clearUnusedFeatureData()
            }
            OptimizationLevel.HEAVY -> {
                // 清理所有可清理数据
                clearAllCleanableData()
            }
        }
    }
    
    override fun canOptimize(): Boolean = true
}
```

## 性能优化建议

### 1. 内存使用优化

- 及时释放不再使用的对象引用
- 使用弱引用避免循环引用
- 合理设置缓存大小和过期时间
- 避免在内存紧张时创建大对象

### 2. 缓存策略优化

- 根据数据访问频率选择合适的缓存类型
- 设置合理的缓存过期时间
- 定期清理过期缓存
- 监控缓存命中率

### 3. 内存泄漏预防

- 在 Activity/Fragment 销毁时取消网络请求
- 及时移除监听器和回调
- 避免在静态变量中持有 Context 引用
- 使用 Application Context 替代 Activity Context

## 故障排除

### 1. 内存泄漏检测不工作

检查是否正确初始化：

```kotlin
// 确保在 Application 中正确初始化
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MemoryLeakDetector.init(this, enabled = true)
    }
}
```

### 2. 内存优化效果不明显

调整优化策略和阈值：

```kotlin
// 降低内存阈值，更频繁地触发优化
MemoryOptimizer.configure(
    lowThreshold = 30 * 1024 * 1024L,    // 30MB
    criticalThreshold = 10 * 1024 * 1024L // 10MB
)
```

### 3. 缓存清理过于频繁

调整缓存配置：

```kotlin
// 增加缓存大小，减少清理频率
CacheManager.configureMemoryCache(
    maxSize = 100 * 1024 * 1024L,  // 100MB
    maxEntries = 200
)
```

## 总结

MateLink 的内存管理解决方案提供了完整的内存监控、泄漏检测和优化功能。通过合理使用这些工具，可以有效提升应用的内存使用效率，减少内存泄漏，提升用户体验。

建议在开发过程中：

1. 始终启用内存泄漏检测
2. 根据应用特点配置合适的优化策略
3. 定期检查内存使用情况
4. 在发布前进行充分的内存测试

通过持续的内存管理优化，可以确保应用在各种设备上都能稳定运行。
