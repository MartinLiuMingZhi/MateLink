# MateLink - Coil图片框架使用指南

## 🎨 概述

MateLink使用Coil作为图片加载框架，提供高性能、内存友好的图片加载解决方案，完美支持Jetpack Compose，为用户提供流畅的图片浏览体验。

## ✨ 核心特性

- 🚀 **Compose原生支持** - 为Jetpack Compose量身定制
- 📦 **轻量级设计** - 仅200KB包大小
- 💾 **智能缓存** - 内存+磁盘双重缓存
- ⚡ **高性能** - 协程异步加载
- 🔧 **易于定制** - 丰富的配置选项
- 🎯 **内存优化** - 根据内存状态动态调整

## 🏗️ 架构设计

```
Coil Image System
├── ImageLoaderManager (加载器管理)
│   ├── defaultImageLoader (默认加载器)
│   ├── avatarImageLoader (头像专用)
│   ├── largeImageLoader (大图专用)
│   └── thumbnailImageLoader (缩略图专用)
├── ImageComponents (UI组件)
│   ├── UserAvatar (用户头像)
│   ├── ChatImage (聊天图片)
│   ├── SpaceAvatar (空间头像)
│   ├── MomentImageGrid (朋友圈图片网格)
│   └── ImagePreview (图片预览)
├── ImageCacheManager (缓存管理)
├── ImageUtils (工具类)
└── ImageModule (依赖注入)
```

## 🚀 快速开始

### 1. 基础使用

```kotlin
@Composable
fun SimpleImageExample() {
    AsyncImage(
        model = "https://example.com/image.jpg",
        contentDescription = "示例图片",
        modifier = Modifier.size(200.dp)
    )
}
```

### 2. 用户头像

```kotlin
@Composable
fun UserProfileCard(user: User) {
    Row {
        UserAvatar(
            avatarUrl = user.avatar,
            size = 48.dp,
            showOnlineStatus = true,
            isOnline = user.isOnline,
            onClick = { /* 查看大图 */ }
        )
        
        Column {
            Text(user.name)
            Text(user.status)
        }
    }
}
```

### 3. 聊天图片

```kotlin
@Composable
fun ChatMessageImage(message: ChatMessage) {
    ChatImage(
        imageUrl = message.imageUrl,
        maxWidth = 200.dp,
        maxHeight = 200.dp,
        onClick = { 
            // 打开图片预览
            openImagePreview(message.imageUrl)
        }
    )
}
```

### 4. 朋友圈图片网格

```kotlin
@Composable
fun MomentCard(moment: Moment) {
    Column {
        Text(moment.content)
        
        if (moment.images.isNotEmpty()) {
            MomentImageGrid(
                images = moment.images,
                onImageClick = { index, allImages ->
                    openImagePreview(allImages, index)
                }
            )
        }
    }
}
```

## 🔧 高级配置

### 1. 自定义ImageLoader

```kotlin
val customImageLoader = ImageLoader.Builder(context)
    .memoryCache {
        MemoryCache.Builder(context)
            .maxSizePercent(0.25) // 使用25%内存
            .strongReferencesEnabled(false)
            .build()
    }
    .diskCache {
        DiskCache.Builder()
            .directory(context.cacheDir.resolve("custom_cache"))
            .maxSizeBytes(50 * 1024 * 1024) // 50MB
            .build()
    }
    .crossfade(true)
    .respectCacheHeaders(false)
    .allowHardware(true)
    .build()
```

### 2. 图片变换

```kotlin
@Composable
fun TransformedImage() {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://example.com/image.jpg")
            .transformations(
                CircleCropTransformation(),
                BlurTransformation(context, 10f),
                GrayscaleTransformation()
            )
            .build(),
        contentDescription = "变换后的图片"
    )
}
```

### 3. 内存优化加载

```kotlin
@Composable
fun MemoryAwareImage(imageUrl: String) {
    val memoryStatus = remember { getMemoryStatus() }
    
    val imageSize = when (memoryStatus) {
        MemoryStatus.GOOD -> 800
        MemoryStatus.MODERATE -> 600
        MemoryStatus.HIGH -> 400
        MemoryStatus.CRITICAL -> 200
    }
    
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(imageSize, imageSize)
            .build(),
        contentDescription = "内存优化图片"
    )
}
```

## 📱 MateLink专用组件

### 1. 消息图片组件

```kotlin
@Composable
fun MessageImageBubble(
    imageUrl: String,
    isSent: Boolean,
    onClick: () -> Unit
) {
    ChatImage(
        imageUrl = imageUrl,
        maxWidth = if (isSent) 180.dp else 200.dp,
        cornerRadius = if (isSent) 12.dp else 8.dp,
        onClick = onClick
    )
}
```

### 2. 空间头像组件

```kotlin
@Composable
fun SpaceListItem(space: Space) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpaceAvatar(
            avatarUrl = space.avatar,
            spaceName = space.name,
            size = 56.dp,
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(space.name)
            Text("${space.memberCount}名成员")
        }
    }
}
```

### 3. 图片预览功能

```kotlin
@Composable
fun ImagePreviewExample() {
    var showPreview by remember { mutableStateOf(false) }
    val images = listOf(
        "https://example.com/image1.jpg",
        "https://example.com/image2.jpg",
        "https://example.com/image3.jpg"
    )
    
    // 触发预览
    Button(onClick = { showPreview = true }) {
        Text("查看图片")
    }
    
    // 图片预览对话框
    if (showPreview) {
        ImagePreview(
            images = images,
            initialIndex = 0,
            onDismiss = { showPreview = false },
            onDownload = { url ->
                // 下载图片
                downloadImage(url)
            },
            onShare = { url ->
                // 分享图片
                shareImage(url)
            }
        )
    }
}
```

## 🛠️ 缓存管理

### 1. 查看缓存状态

```kotlin
@Composable
fun CacheStatusDisplay() {
    val imageCacheManager = hiltViewModel<ImageCacheManager>()
    val cacheStats by imageCacheManager.getCacheStats().collectAsState()
    
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("图片缓存状态")
            Text("内存缓存: ${formatBytes(cacheStats.getTotalMemorySize())}")
            Text("磁盘缓存: ${formatBytes(cacheStats.getTotalDiskSize())}")
            Text("总缓存: ${formatBytes(cacheStats.getTotalSize())}")
        }
    }
}
```

### 2. 缓存清理

```kotlin
// 清理所有缓存
imageCacheManager.clearAllCache()

// 仅清理内存缓存
imageCacheManager.clearMemoryCache()

// 仅清理磁盘缓存
imageCacheManager.clearDiskCache()

// 根据内存压力清理
imageCacheManager.cleanupByMemoryPressure(memoryStatus)
```

### 3. 预加载优化

```kotlin
// 预加载单张图片
imageCacheManager.preloadImage("https://example.com/image.jpg")

// 批量预加载
val imageUrls = listOf("url1", "url2", "url3")
imageCacheManager.preloadImages(imageUrls, maxConcurrent = 3)
```

## ⚡ 性能优化技巧

### 1. 内存优化

```kotlin
// 根据内存状态动态调整图片质量
@Composable
fun OptimizedImage(imageUrl: String) {
    val memoryUtils = hiltViewModel<MemoryUtils>()
    val recommendedSize = getRecommendedImageSize(memoryUtils.getMemoryStatus())
    
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(recommendedSize, recommendedSize)
            .build(),
        contentDescription = "优化图片"
    )
}
```

### 2. 网络优化

```kotlin
// 根据网络状态调整加载策略
@Composable
fun NetworkAwareImage(imageUrl: String) {
    val networkMonitor = hiltViewModel<NetworkMonitor>()
    val networkStatus by networkMonitor.networkStatus.collectAsState()
    
    val imageRequest = when (networkStatus) {
        NetworkStatus.CONNECTED_WIFI -> {
            // WiFi环境：加载高质量图片
            ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .size(1080, 1080)
                .build()
        }
        NetworkStatus.CONNECTED_MOBILE -> {
            // 移动网络：加载中等质量图片
            ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .size(600, 600)
                .build()
        }
        else -> {
            // 无网络：仅显示缓存图片
            ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .networkCachePolicy(coil.request.CachePolicy.DISABLED)
                .build()
        }
    }
    
    AsyncImage(
        model = imageRequest,
        contentDescription = "网络感知图片"
    )
}
```

### 3. 列表优化

```kotlin
@Composable
fun OptimizedImageList(items: List<ImageItem>) {
    LazyColumn {
        items(items) { item ->
            // 使用缩略图加载器提升列表滚动性能
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.thumbnailUrl)
                    .size(100, 100)
                    .crossfade(false) // 列表中关闭动画提升性能
                    .build(),
                contentDescription = "列表图片",
                imageLoader = LocalImageLoader.current.thumbnailImageLoader
            )
        }
    }
}
```

## 🎯 最佳实践

### 1. 图片加载最佳实践

```kotlin
// ✅ 推荐：明确指定图片尺寸
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .size(300, 300) // 明确尺寸，避免内存浪费
        .build(),
    contentDescription = "图片"
)

// ❌ 避免：不指定尺寸
AsyncImage(
    model = imageUrl, // 可能加载原始大图，浪费内存
    contentDescription = "图片"
)
```

### 2. 缓存策略

```kotlin
// ✅ 推荐：为不同场景使用不同的缓存策略
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .memoryCachePolicy(CachePolicy.ENABLED)  // 启用内存缓存
        .diskCachePolicy(CachePolicy.ENABLED)    // 启用磁盘缓存
        .networkCachePolicy(CachePolicy.ENABLED) // 启用网络缓存
        .build()
)

// 临时图片：不缓存
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(temporaryImageUrl)
        .memoryCachePolicy(CachePolicy.DISABLED)
        .diskCachePolicy(CachePolicy.DISABLED)
        .build()
)
```

### 3. 错误处理

```kotlin
@Composable
fun RobustImage(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "图片",
        placeholder = painterResource(R.drawable.placeholder),
        error = painterResource(R.drawable.error),
        fallback = painterResource(R.drawable.fallback),
        onLoading = { 
            // 加载开始
        },
        onSuccess = { 
            // 加载成功
        },
        onError = { 
            // 加载失败
        }
    )
}
```

## 📊 性能监控

### 1. 缓存监控

```kotlin
@Composable
fun CacheMonitorScreen() {
    val imageCacheManager = hiltViewModel<ImageCacheManager>()
    val cacheStats by remember { 
        derivedStateOf { imageCacheManager.getCacheStats() }
    }.collectAsState()
    
    LazyColumn {
        item {
            CacheStatsCard(
                title = "默认缓存",
                cacheInfo = cacheStats.defaultCache
            )
        }
        
        item {
            CacheStatsCard(
                title = "头像缓存", 
                cacheInfo = cacheStats.avatarCache
            )
        }
        
        // 其他缓存统计...
    }
}
```

### 2. 内存监控

```kotlin
@Composable
fun MemoryMonitorCard() {
    val memoryUtils = hiltViewModel<MemoryUtils>()
    val memoryInfo by remember {
        derivedStateOf { memoryUtils.getHeapMemoryInfo() }
    }.collectAsState()
    
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("内存使用情况")
            Text("已使用: ${formatBytes(memoryInfo.usedMemory)}")
            Text("可用: ${formatBytes(memoryInfo.freeMemory)}")
            Text("最大: ${formatBytes(memoryInfo.maxMemory)}")
            
            LinearProgressIndicator(
                progress = memoryInfo.usedMemory.toFloat() / memoryInfo.maxMemory,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

## 🛡️ 内存保护机制

### 1. 自动内存清理

```kotlin
class MemoryProtectionManager @Inject constructor(
    private val imageCacheManager: ImageCacheManager,
    private val memoryUtils: MemoryUtils
) {
    
    fun checkAndCleanupMemory() {
        val memoryStatus = memoryUtils.getMemoryStatus()
        
        when (memoryStatus) {
            MemoryStatus.CRITICAL -> {
                // 危险：立即清理所有图片缓存
                imageCacheManager.clearMemoryCache()
                System.gc()
            }
            MemoryStatus.HIGH -> {
                // 高使用率：清理大图缓存
                imageCacheManager.clearLargeImageCache()
            }
            MemoryStatus.MODERATE -> {
                // 中等：清理缩略图缓存
                imageCacheManager.clearThumbnailCache()
            }
            else -> {
                // 正常：无需处理
            }
        }
    }
}
```

### 2. 智能加载策略

```kotlin
@Composable
fun SmartImage(imageUrl: String) {
    val memoryStatus = getMemoryStatus()
    val networkStatus = getNetworkStatus()
    
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .size(
            when {
                memoryStatus == MemoryStatus.CRITICAL -> 200
                networkStatus == NetworkStatus.CONNECTED_MOBILE -> 400
                else -> 600
            }
        )
        .build()
    
    AsyncImage(
        model = imageRequest,
        contentDescription = "智能加载图片"
    )
}
```

## 🎪 实际应用示例

### 聊天界面完整示例

```kotlin
@Composable
fun ChatScreen(messages: List<ChatMessage>) {
    LazyColumn {
        items(messages) { message ->
            when (message.type) {
                MessageType.TEXT -> {
                    TextMessage(message)
                }
                MessageType.IMAGE -> {
                    Row(
                        horizontalArrangement = if (message.isSent) {
                            Arrangement.End
                        } else {
                            Arrangement.Start
                        }
                    ) {
                        ChatImage(
                            imageUrl = message.imageUrl,
                            maxWidth = 200.dp,
                            cornerRadius = 12.dp,
                            onClick = {
                                // 打开图片预览
                                openImagePreview(message.imageUrl)
                            }
                        )
                    }
                }
            }
        }
    }
}
```

### 朋友圈界面完整示例

```kotlin
@Composable
fun MomentListScreen(moments: List<Moment>) {
    LazyColumn {
        items(moments) { moment ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 用户信息
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(
                            avatarUrl = moment.author.avatar,
                            size = 40.dp
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(moment.author.name)
                            Text(moment.createdAt)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 内容
                    Text(moment.content)
                    
                    // 图片
                    if (moment.images.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        MomentImageGrid(
                            images = moment.images,
                            onImageClick = { index, allImages ->
                                openImagePreview(allImages, index)
                            }
                        )
                    }
                }
            }
        }
    }
}
```

## 🎯 总结

Coil为MateLink提供了：

1. **完美的技术匹配** - Kotlin + Compose + 协程
2. **优秀的性能表现** - 内存友好，加载快速
3. **丰富的功能特性** - 变换、缓存、预览等
4. **简洁的API设计** - 易学易用，代码简洁
5. **灵活的扩展能力** - 支持自定义配置和组件

通过这套完整的图片解决方案，MateLink可以为用户提供流畅、高效的图片浏览体验，同时保证应用的性能和稳定性。

---

**建议**：在实际使用中，根据具体场景选择合适的ImageLoader和缓存策略，定期监控内存使用情况并进行优化。
