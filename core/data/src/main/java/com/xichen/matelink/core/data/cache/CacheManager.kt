package com.xichen.matelink.core.data.cache

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.jakewharton.disklrucache.DiskLruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 缓存管理器
 * 统一管理内存缓存和磁盘缓存
 */
@Singleton
class CacheManager @Inject constructor(
    private val context: Context
) {
    
    // 内存缓存配置
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8 // 使用1/8的可用内存
    
    // 图片内存缓存
    private val bitmapMemoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }
    
    // 数据内存缓存
    private val dataMemoryCache = LruCache<String, Any>(100)
    
    // 磁盘缓存
    private val diskCache: DiskLruCache by lazy {
        DiskLruCache.open(
            File(context.cacheDir, "app_cache"),
            1,
            1,
            50 * 1024 * 1024 // 50MB
        )
    }
    
    /**
     * 图片缓存操作
     */
    fun getBitmap(key: String): Bitmap? {
        return bitmapMemoryCache.get(key)
    }
    
    fun putBitmap(key: String, bitmap: Bitmap) {
        bitmapMemoryCache.put(key, bitmap)
    }
    
    fun removeBitmap(key: String) {
        bitmapMemoryCache.remove(key)
    }
    
    /**
     * 数据缓存操作
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getData(key: String): T? {
        return dataMemoryCache.get(key) as? T
    }
    
    fun <T> putData(key: String, data: T) {
        dataMemoryCache.put(key, data as Any)
    }
    
    fun removeData(key: String) {
        dataMemoryCache.remove(key)
    }
    
    /**
     * 磁盘缓存操作
     */
    suspend fun getDiskCache(key: String): String? = withContext(Dispatchers.IO) {
        try {
            diskCache.get(key)?.getString(0)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun putDiskCache(key: String, value: String) = withContext(Dispatchers.IO) {
        try {
            val editor = diskCache.edit(key)
            editor?.set(0, value)
            editor?.commit()
        } catch (e: Exception) {
            // 忽略缓存失败
        }
    }
    
    suspend fun removeDiskCache(key: String) = withContext(Dispatchers.IO) {
        try {
            diskCache.remove(key)
        } catch (e: Exception) {
            // 忽略删除失败
        }
    }
    
    /**
     * 获取缓存统计信息
     */
    fun getCacheStats(): CacheStats {
        val memorySize = bitmapMemoryCache.size()
        val memoryMaxSize = bitmapMemoryCache.maxSize()
        val memoryHitCount = bitmapMemoryCache.hitCount()
        val memoryMissCount = bitmapMemoryCache.missCount()
        
        val diskSize = try {
            diskCache.size()
        } catch (e: Exception) {
            0L
        }
        
        val diskMaxSize = try {
            diskCache.maxSize
        } catch (e: Exception) {
            0L
        }
        
        return CacheStats(
            memorySize = memorySize,
            memoryMaxSize = memoryMaxSize,
            memoryHitCount = memoryHitCount,
            memoryMissCount = memoryMissCount,
            diskSize = diskSize,
            diskMaxSize = diskMaxSize
        )
    }
    
    /**
     * 清理所有缓存
     */
    suspend fun clearAllCache() {
        // 清理内存缓存
        bitmapMemoryCache.evictAll()
        dataMemoryCache.evictAll()
        
        // 清理磁盘缓存
        withContext(Dispatchers.IO) {
            try {
                diskCache.delete()
            } catch (e: Exception) {
                // 忽略删除失败
            }
        }
    }
    
    /**
     * 清理过期缓存
     */
    suspend fun clearExpiredCache() = withContext(Dispatchers.IO) {
        try {
            // 清理7天前的磁盘缓存
            val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
            diskCache.evictAll() // 简化实现，实际可以根据时间戳清理
        } catch (e: Exception) {
            // 忽略清理失败
        }
    }
    
    /**
     * 获取缓存大小（格式化）
     */
    fun getFormattedCacheSize(): String {
        val stats = getCacheStats()
        val totalBytes = (stats.memorySize * 1024) + stats.diskSize
        return formatBytes(totalBytes)
    }
    
    /**
     * 格式化字节数
     */
    private fun formatBytes(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        
        return when {
            gb >= 1 -> "${decimalFormat.format(gb)} GB"
            mb >= 1 -> "${decimalFormat.format(mb)} MB"
            kb >= 1 -> "${decimalFormat.format(kb)} KB"
            else -> "$bytes B"
        }
    }
}

/**
 * 缓存统计信息
 */
data class CacheStats(
    val memorySize: Int,        // 内存缓存大小（KB）
    val memoryMaxSize: Int,     // 内存缓存最大大小（KB）
    val memoryHitCount: Long,   // 内存缓存命中次数
    val memoryMissCount: Long,  // 内存缓存未命中次数
    val diskSize: Long,         // 磁盘缓存大小（字节）
    val diskMaxSize: Long       // 磁盘缓存最大大小（字节）
) {
    val memoryHitRate: Float
        get() = if (memoryHitCount + memoryMissCount > 0) {
            (memoryHitCount.toFloat() / (memoryHitCount + memoryMissCount)) * 100
        } else 0f
}

/**
 * 应用内存信息
 */
data class AppMemoryInfo(
    val totalPss: Int,          // 总PSS内存（KB）
    val totalPrivateDirty: Int, // 私有脏内存（KB）
    val totalSharedDirty: Int,  // 共享脏内存（KB）
    val nativeHeap: Int,        // Native堆内存（KB）
    val dalvikHeap: Int,        // Dalvik堆内存（KB）
    val otherPss: Int           // 其他PSS内存（KB）
)

/**
 * 系统内存信息
 */
data class SystemMemoryInfo(
    val availMem: Long,         // 可用内存（字节）
    val totalMem: Long,         // 总内存（字节）
    val threshold: Long,        // 内存阈值（字节）
    val lowMemory: Boolean      // 是否内存不足
)

/**
 * 堆内存信息
 */
data class HeapMemoryInfo(
    val maxMemory: Long,        // 最大可用内存（字节）
    val totalMemory: Long,      // 总分配内存（字节）
    val freeMemory: Long,       // 空闲内存（字节）
    val usedMemory: Long        // 已使用内存（字节）
)
