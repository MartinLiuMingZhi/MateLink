# MateLink - 内存存储方案设计

## 🏗️ 存储架构设计

### 多层存储架构
```
┌─────────────────────────────────────┐
│           UI Layer                  │  ← StateFlow/LiveData
├─────────────────────────────────────┤
│         Memory Cache                │  ← LRU内存缓存
├─────────────────────────────────────┤
│        Persistent Storage           │  ← Room + DataStore
├─────────────────────────────────────┤
│         File Cache                  │  ← DiskLruCache
├─────────────────────────────────────┤
│        Secure Storage               │  ← EncryptedPreferences
└─────────────────────────────────────┘
```

## 📋 **推荐存储方案**

### 1. **Room数据库** - 结构化数据存储
**适用场景：** 聊天消息、用户信息、空间数据、朋友圈动态

**优势：**
- ✅ 强大的SQL查询能力
- ✅ 类型安全的编译时检查
- ✅ 支持复杂关系和事务
- ✅ 自动数据库迁移
- ✅ 协程和Flow支持

**使用示例：**
```kotlin
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val content: String,
    val senderId: String,
    val spaceId: String,
    val timestamp: Long,
    val type: MessageType
)

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE spaceId = :spaceId ORDER BY timestamp DESC")
    fun getMessagesBySpace(spaceId: String): Flow<List<MessageEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
}
```

### 2. **DataStore** - 配置和偏好设置
**适用场景：** 用户偏好、应用设置、主题配置

**优势：**
- ✅ 类型安全
- ✅ 协程支持
- ✅ 数据一致性
- ✅ 自动序列化

**使用示例：**
```kotlin
@Serializable
data class UserPreferences(
    val themeMode: String = "system",
    val language: String = "zh",
    val notificationEnabled: Boolean = true,
    val autoDownloadImages: Boolean = true
)

class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    
    val userPreferences: Flow<UserPreferences> = dataStore.data
        .map { preferences ->
            UserPreferences(
                themeMode = preferences[THEME_MODE] ?: "system",
                language = preferences[LANGUAGE] ?: "zh"
            )
        }
}
```

### 3. **EncryptedSharedPreferences** - 安全存储
**适用场景：** 登录Token、密码、敏感信息

**优势：**
- ✅ 自动加密
- ✅ 安全性高
- ✅ 简单易用

**使用示例：**
```kotlin
class SecureStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString("access_token", token)
            .apply()
    }
}
```

### 4. **LRU内存缓存** - 高频数据缓存
**适用场景：** 用户头像、聊天图片、临时数据

**优势：**
- ✅ 访问速度极快
- ✅ 自动内存管理
- ✅ LRU淘汰策略

**使用示例：**
```kotlin
class MemoryCache<K, V>(maxSize: Int) {
    private val cache = LruCache<K, V>(maxSize)
    
    fun get(key: K): V? = cache.get(key)
    
    fun put(key: K, value: V) = cache.put(key, value)
    
    fun remove(key: K) = cache.remove(key)
    
    fun clear() = cache.evictAll()
}

// 图片缓存示例
class ImageCache @Inject constructor() {
    private val memoryCache = MemoryCache<String, Bitmap>(
        maxSize = (Runtime.getRuntime().maxMemory() / 8).toInt()
    )
    
    fun getImage(url: String): Bitmap? = memoryCache.get(url)
    fun putImage(url: String, bitmap: Bitmap) = memoryCache.put(url, bitmap)
}
```

### 5. **DiskLruCache** - 文件缓存
**适用场景：** 图片文件、音频文件、视频缓存

**优势：**
- ✅ 自动磁盘空间管理
- ✅ LRU淘汰策略
- ✅ 线程安全

**使用示例：**
```kotlin
class DiskCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val diskCache = DiskLruCache.open(
        File(context.cacheDir, "disk_cache"),
        1,
        1,
        50 * 1024 * 1024 // 50MB
    )
    
    suspend fun get(key: String): String? = withContext(Dispatchers.IO) {
        diskCache.get(key)?.getString(0)
    }
    
    suspend fun put(key: String, value: String) = withContext(Dispatchers.IO) {
        val editor = diskCache.edit(key)
        editor?.set(0, value)
        editor?.commit()
    }
}
```

## 🎯 **针对MateLink的具体存储方案**

### 1. **聊天数据存储**

```kotlin
// Room数据库 - 主要存储
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val content: String,
    val type: MessageType,
    val senderId: String,
    val spaceId: String,
    val timestamp: Long,
    val status: MessageStatus,
    // 索引优化
    @ColumnInfo(index = true) val conversationId: String
)

// 内存缓存 - 最近消息
class ChatMemoryCache @Inject constructor() {
    private val recentMessages = LruCache<String, List<Message>>(100)
    
    fun getRecentMessages(conversationId: String): List<Message>? {
        return recentMessages.get(conversationId)
    }
    
    fun cacheRecentMessages(conversationId: String, messages: List<Message>) {
        recentMessages.put(conversationId, messages)
    }
}
```

### 2. **用户设置存储**

```kotlin
// DataStore - 用户偏好设置
@Serializable
data class AppSettings(
    val themeMode: String = "system",
    val language: String = "zh",
    val fontSize: Float = 16f,
    val notificationEnabled: Boolean = true,
    val autoDownloadImages: Boolean = true,
    val autoDownloadVideos: Boolean = false,
    val messageSound: Boolean = true,
    val vibration: Boolean = true
)

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    
    val settings: Flow<AppSettings> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            AppSettings(
                themeMode = preferences[THEME_MODE] ?: "system",
                language = preferences[LANGUAGE] ?: "zh",
                fontSize = preferences[FONT_SIZE] ?: 16f
            )
        }
    
    suspend fun updateTheme(themeMode: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode
        }
    }
}
```

### 3. **文件缓存存储**

```kotlin
// 图片文件缓存
class ImageCacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 内存缓存 - 快速访问
    private val memoryCache = LruCache<String, Bitmap>(
        (Runtime.getRuntime().maxMemory() / 8).toInt()
    )
    
    // 磁盘缓存 - 持久化
    private val diskCache = DiskLruCache.open(
        File(context.cacheDir, "images"),
        1, 1, 100 * 1024 * 1024 // 100MB
    )
    
    suspend fun getImage(url: String): Bitmap? {
        // 先从内存缓存获取
        memoryCache.get(url)?.let { return it }
        
        // 再从磁盘缓存获取
        return withContext(Dispatchers.IO) {
            diskCache.get(url)?.let { snapshot ->
                val bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0))
                memoryCache.put(url, bitmap)
                bitmap
            }
        }
    }
    
    suspend fun putImage(url: String, bitmap: Bitmap) {
        // 存入内存缓存
        memoryCache.put(url, bitmap)
        
        // 存入磁盘缓存
        withContext(Dispatchers.IO) {
            val editor = diskCache.edit(url)
            editor?.let {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, it.newOutputStream(0))
                it.commit()
            }
        }
    }
}
```

### 4. **安全数据存储**

```kotlin
// 敏感信息加密存储
class SecureDataManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        "secure_data",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    // Token存储
    fun saveAuthToken(token: String) {
        encryptedPrefs.edit()
            .putString("auth_token", token)
            .apply()
    }
    
    fun getAuthToken(): String? {
        return encryptedPrefs.getString("auth_token", null)
    }
    
    // 生物识别数据
    fun saveBiometricKey(key: String) {
        encryptedPrefs.edit()
            .putString("biometric_key", key)
            .apply()
    }
}
```

## 🚀 **推荐的存储技术栈**

### **主要技术选择：**

1. **Room 2.5.2+** - 主数据库
   - 聊天消息存储
   - 用户信息缓存
   - 空间数据管理
   - 朋友圈内容

2. **DataStore 1.0.0** - 设置存储
   - 用户偏好设置
   - 应用配置
   - 主题设置

3. **EncryptedSharedPreferences** - 安全存储
   - 登录Token
   - 生物识别数据
   - 其他敏感信息

4. **LruCache** - 内存缓存
   - 图片缓存
   - 用户头像
   - 临时数据

5. **DiskLruCache** - 文件缓存
   - 图片文件
   - 音频文件
   - 视频缓存

### **存储层次结构：**

```
Application Memory
├── UI State (StateFlow/LiveData)
├── Memory Cache (LruCache)
│   ├── User Avatars
│   ├── Recent Messages  
│   └── Temporary Data
├── Database (Room)
│   ├── Messages Table
│   ├── Users Table
│   ├── Spaces Table
│   └── Moments Table
├── Preferences (DataStore)
│   ├── User Settings
│   ├── App Config
│   └── Theme Settings
├── Secure Storage (EncryptedPrefs)
│   ├── Auth Tokens
│   └── Sensitive Data
└── File Cache (DiskLruCache)
    ├── Images Cache
    ├── Audio Cache
    └── Video Cache
```

## 🎯 **具体实现建议**

### **1. 聊天消息存储**
```kotlin
// 三层缓存策略
class MessageRepository {
    // L1: 内存缓存（最快）
    private val memoryCache = LruCache<String, List<Message>>(50)
    
    // L2: Room数据库（持久化）
    private val messageDao: MessageDao
    
    // L3: 网络请求（最新数据）
    private val chatApi: ChatApi
    
    suspend fun getMessages(conversationId: String): Flow<List<Message>> {
        return flow {
            // 1. 先从内存缓存获取
            memoryCache.get(conversationId)?.let { emit(it) }
            
            // 2. 从数据库获取
            val dbMessages = messageDao.getMessages(conversationId)
            emit(dbMessages)
            memoryCache.put(conversationId, dbMessages)
            
            // 3. 从网络获取最新数据
            try {
                val networkMessages = chatApi.getMessages(conversationId)
                messageDao.insertMessages(networkMessages)
                memoryCache.put(conversationId, networkMessages)
                emit(networkMessages)
            } catch (e: Exception) {
                // 网络失败时使用缓存数据
            }
        }
    }
}
```

### **2. 文件存储管理**
```kotlin
class FileStorageManager {
    // 图片：内存 + 磁盘双重缓存
    private val imageMemoryCache = LruCache<String, Bitmap>(memorySize)
    private val imageDiskCache = DiskLruCache(diskSize)
    
    // 音频：仅磁盘缓存
    private val audioDiskCache = DiskLruCache(audioSize)
    
    // 视频：按需下载，不缓存
    suspend fun downloadVideo(url: String): File {
        return downloadManager.download(url)
    }
}
```

### **3. 用户数据管理**
```kotlin
class UserDataManager {
    // 当前用户信息：DataStore
    val currentUser: Flow<User> = userPrefsDataStore.data
    
    // 其他用户信息：Room + 内存缓存
    private val userCache = LruCache<String, User>(100)
    
    suspend fun getUser(userId: String): User {
        // 内存缓存 -> Room数据库 -> 网络请求
        return userCache.get(userId) 
            ?: userDao.getUser(userId) 
            ?: userApi.getUser(userId)
    }
}
```

## 🔧 **性能优化建议**

### **1. 内存使用优化**
- 图片内存缓存：不超过可用内存的1/8
- 消息内存缓存：最近50个会话
- 定期清理过期缓存

### **2. 磁盘空间管理**
- 图片缓存：100MB上限
- 音频缓存：50MB上限
- 数据库：定期清理老旧数据

### **3. 网络优化**
- 优先使用缓存数据
- 后台同步最新数据
- 智能预加载策略

## 🎨 **KMP兼容性考虑**

为了后期KMP迁移，建议：

1. **使用接口抽象**
```kotlin
interface StorageManager {
    suspend fun save(key: String, value: String)
    suspend fun get(key: String): String?
}

// Android实现
class AndroidStorageManager : StorageManager {
    // SharedPreferences实现
}

// 未来iOS实现
class IOSStorageManager : StorageManager {
    // UserDefaults实现
}
```

2. **数据模型纯Kotlin**
```kotlin
@Serializable
data class User(
    val id: String,
    val name: String,
    val avatar: String?
) // 无Android依赖，KMP友好
```

3. **Repository模式**
```kotlin
interface UserRepository {
    suspend fun getUser(id: String): User
    suspend fun saveUser(user: User)
}
// 接口定义KMP兼容，实现可平台特定
```

## 💡 **最终推荐**

基于MateLink的需求，推荐使用以下组合：

```kotlin
dependencies {
    // 主数据库
    implementation "androidx.room:room-runtime:2.5.2"
    implementation "androidx.room:room-ktx:2.5.2"
    
    // 设置存储
    implementation "androidx.datastore:datastore-preferences:1.0.0"
    
    // 安全存储
    implementation "androidx.security:security-crypto:1.1.0-alpha06"
    
    // 文件缓存
    implementation "com.jakewharton:disklrucache:2.0.2"
    
    // 图片加载（内置缓存）
    implementation "io.coil-kt:coil-compose:2.4.0"
}
```

这个方案既满足当前Android开发需求，又为未来KMP迁移预留了良好的架构基础！
