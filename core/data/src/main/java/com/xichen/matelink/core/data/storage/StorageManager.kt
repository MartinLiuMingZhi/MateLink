package com.xichen.matelink.core.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 统一存储管理器
 * 整合各种存储方案，提供统一的存储接口
 */

// DataStore扩展
private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Singleton
class StorageManager @Inject constructor(
    private val context: Context
) {
    
    // JSON序列化器
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    // 加密存储
    private val encryptedPrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        EncryptedSharedPreferences.create(
            context,
            "secure_storage",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    // ========== DataStore操作 ==========
    
    /**
     * 保存应用设置
     */
    suspend fun saveAppSetting(key: String, value: String) {
        context.appDataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }
    
    suspend fun saveAppSetting(key: String, value: Int) {
        context.appDataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }
    
    suspend fun saveAppSetting(key: String, value: Boolean) {
        context.appDataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }
    
    suspend fun saveAppSetting(key: String, value: Float) {
        context.appDataStore.edit { preferences ->
            preferences[floatPreferencesKey(key)] = value
        }
    }
    
    /**
     * 获取应用设置
     */
    fun getAppSetting(key: String, defaultValue: String): Flow<String> {
        return context.appDataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: defaultValue
        }
    }
    
    fun getAppSetting(key: String, defaultValue: Int): Flow<Int> {
        return context.appDataStore.data.map { preferences ->
            preferences[intPreferencesKey(key)] ?: defaultValue
        }
    }
    
    fun getAppSetting(key: String, defaultValue: Boolean): Flow<Boolean> {
        return context.appDataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)] ?: defaultValue
        }
    }
    
    fun getAppSetting(key: String, defaultValue: Float): Flow<Float> {
        return context.appDataStore.data.map { preferences ->
            preferences[floatPreferencesKey(key)] ?: defaultValue
        }
    }
    
    /**
     * 保存用户设置
     */
    suspend fun saveUserSetting(key: String, value: String) {
        context.userDataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }
    
    /**
     * 获取用户设置
     */
    fun getUserSetting(key: String, defaultValue: String): Flow<String> {
        return context.userDataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: defaultValue
        }
    }
    
    /**
     * 保存复杂对象到DataStore
     */
    suspend inline fun <reified T> saveObject(key: String, obj: T) {
        val jsonString = json.encodeToString(obj)
        context.appDataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = jsonString
        }
    }
    
    /**
     * 从DataStore获取复杂对象
     */
    inline fun <reified T> getObject(key: String): Flow<T?> {
        return context.appDataStore.data.map { preferences ->
            try {
                preferences[stringPreferencesKey(key)]?.let { jsonString ->
                    json.decodeFromString<T>(jsonString)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    // ========== 安全存储操作 ==========
    
    /**
     * 保存敏感数据
     */
    fun saveSecureData(key: String, value: String) {
        encryptedPrefs.edit()
            .putString(key, value)
            .apply()
    }
    
    /**
     * 获取敏感数据
     */
    fun getSecureData(key: String, defaultValue: String? = null): String? {
        return encryptedPrefs.getString(key, defaultValue)
    }
    
    /**
     * 删除敏感数据
     */
    fun removeSecureData(key: String) {
        encryptedPrefs.edit()
            .remove(key)
            .apply()
    }
    
    /**
     * 清空所有敏感数据
     */
    fun clearSecureData() {
        encryptedPrefs.edit()
            .clear()
            .apply()
    }
    
    // ========== 存储管理操作 ==========
    
    /**
     * 获取存储使用情况
     */
    fun getStorageUsage(): StorageUsage {
        val internalStorage = context.filesDir.usableSpace
        val externalStorage = context.getExternalFilesDir(null)?.usableSpace ?: 0L
        val cacheStorage = context.cacheDir.usableSpace
        
        val databaseSize = getDatabaseSize()
        val cacheSize = getCacheSize()
        val preferencesSize = getPreferencesSize()
        
        return StorageUsage(
            internalAvailable = internalStorage,
            externalAvailable = externalStorage,
            cacheAvailable = cacheStorage,
            databaseSize = databaseSize,
            cacheSize = cacheSize,
            preferencesSize = preferencesSize
        )
    }
    
    /**
     * 清理存储空间
     */
    suspend fun cleanupStorage() {
        // 清理缓存
        context.cacheDir.deleteRecursively()
        
        // 清理临时文件
        File(context.filesDir, "temp").deleteRecursively()
        
        // 清理过期的外部文件
        context.getExternalFilesDir("temp")?.deleteRecursively()
    }
    
    /**
     * 获取数据库大小
     */
    private fun getDatabaseSize(): Long {
        val dbFile = context.getDatabasePath("app_database")
        return if (dbFile.exists()) dbFile.length() else 0L
    }
    
    /**
     * 获取缓存大小
     */
    private fun getCacheSize(): Long {
        return context.cacheDir.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }
    
    /**
     * 获取偏好设置大小
     */
    private fun getPreferencesSize(): Long {
        val prefsDir = File(context.applicationInfo.dataDir, "shared_prefs")
        return if (prefsDir.exists()) {
            prefsDir.walkTopDown()
                .filter { it.isFile }
                .map { it.length() }
                .sum()
        } else 0L
    }
}

/**
 * 存储使用情况
 */
data class StorageUsage(
    val internalAvailable: Long,    // 内部存储可用空间
    val externalAvailable: Long,    // 外部存储可用空间
    val cacheAvailable: Long,       // 缓存可用空间
    val databaseSize: Long,         // 数据库大小
    val cacheSize: Long,            // 缓存大小
    val preferencesSize: Long       // 偏好设置大小
) {
    val totalUsed: Long
        get() = databaseSize + cacheSize + preferencesSize
}

/**
 * 存储配置
 */
object StorageConfig {
    // 数据库配置
    const val DATABASE_NAME = "matelink_database"
    const val DATABASE_VERSION = 1
    
    // 缓存配置
    const val MEMORY_CACHE_SIZE_RATIO = 8 // 使用1/8的可用内存
    const val DISK_CACHE_SIZE = 50 * 1024 * 1024L // 50MB
    const val IMAGE_CACHE_SIZE = 100 * 1024 * 1024L // 100MB
    
    // 清理配置
    const val CACHE_EXPIRE_DAYS = 7
    const val MAX_DATABASE_SIZE = 500 * 1024 * 1024L // 500MB
    const val MAX_CACHE_SIZE = 200 * 1024 * 1024L // 200MB
}

/**
 * 存储键常量
 */
object StorageKeys {
    // 用户设置
    const val THEME_MODE = "theme_mode"
    const val LANGUAGE = "language"
    const val FONT_SIZE = "font_size"
    const val NOTIFICATION_ENABLED = "notification_enabled"
    
    // 应用设置
    const val AUTO_DOWNLOAD_IMAGES = "auto_download_images"
    const val AUTO_DOWNLOAD_VIDEOS = "auto_download_videos"
    const val CACHE_ENABLED = "cache_enabled"
    const val BACKGROUND_SYNC = "background_sync"
    
    // 安全存储
    const val ACCESS_TOKEN = "access_token"
    const val REFRESH_TOKEN = "refresh_token"
    const val BIOMETRIC_KEY = "biometric_key"
    const val USER_CREDENTIALS = "user_credentials"
}
