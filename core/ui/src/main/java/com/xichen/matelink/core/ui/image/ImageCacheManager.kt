package com.xichen.matelink.core.ui.image

import android.content.Context
import coil.ImageLoader
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.xichen.matelink.core.common.utils.MemoryUtils
import com.xichen.matelink.core.common.utils.MemoryStatus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Singleton

// Import the qualifier annotation
import com.xichen.matelink.core.ui.image.DefaultImageLoader

/**
 * 图片缓存管理器
 * 负责管理Coil的图片缓存
 */

// ImageCacheModule moved to ImageLoaderManager.kt to avoid dependency resolution issues

@Singleton
class ImageCacheManager @javax.inject.Inject constructor(
    private val context: Context,
    private val imageLoader: ImageLoader
) {
    
    private val cacheScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    /**
     * 获取缓存统计信息
     */
    suspend fun getCacheStats(): CacheStats {
        val memoryCache = imageLoader.memoryCache
        val diskCache = imageLoader.diskCache
        
        return CacheStats(
            memoryCacheSize = (memoryCache?.size ?: 0).toLong(),
            memoryCacheMaxSize = (memoryCache?.maxSize ?: 0).toLong(),
            diskCacheSize = (diskCache?.size ?: 0).toLong(),
            diskCacheMaxSize = (diskCache?.maxSize ?: 0).toLong(),
            memoryHitCount = 0L, // Coil doesn't expose hit/miss counts directly
            memoryMissCount = 0L,
            diskHitCount = 0L,
            diskMissCount = 0L
        )
    }
    
    /**
     * 清理内存缓存
     */
    fun clearMemoryCache() {
        imageLoader.memoryCache?.clear()
    }
    
    /**
     * 清理磁盘缓存
     */
    suspend fun clearDiskCache() {
        imageLoader.diskCache?.clear()
    }
    
    /**
     * 清理所有缓存
     */
    suspend fun clearAllCache() {
        clearMemoryCache()
        clearDiskCache()
    }
    
    /**
     * 根据内存状态调整缓存策略
     */
    fun adjustCachePolicy() {
        val memoryUtils = MemoryUtils(context)
        val memoryStatus = memoryUtils.getMemoryStatus()
        
        when (memoryStatus) {
            MemoryStatus.CRITICAL -> {
                // 危险内存时清理内存缓存
                clearMemoryCache()
            }
            MemoryStatus.HIGH -> {
                // 高内存时保持当前策略
            }
            MemoryStatus.MODERATE -> {
                // 中等内存时保持当前策略
            }
            MemoryStatus.GOOD -> {
                // 良好内存时保持当前策略
            }
        }
    }
    
    /**
     * 预加载图片到缓存
     */
    fun preloadImages(urls: List<String>) {
        cacheScope.launch {
            urls.map { url ->
                async {
                    try {
                        imageLoader.enqueue(
                            coil.request.ImageRequest.Builder(context)
                                .data(url)
                                .build()
                        )
                    } catch (e: Exception) {
                        // 预加载失败，忽略错误
                    }
                }
            }.awaitAll()
        }
    }
    
    /**
     * 获取缓存大小（格式化）
     */
    suspend fun getFormattedCacheSize(): String {
        val stats = getCacheStats()
        val totalSize = stats.memoryCacheSize + stats.diskCacheSize
        return formatBytes(totalSize)
    }
    
    /**
     * 格式化字节数
     */
    private fun formatBytes(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = bytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        return String.format("%.2f %s", size, units[unitIndex])
    }
}

/**
 * 缓存统计信息
 */
data class CacheStats(
    val memoryCacheSize: Long,
    val memoryCacheMaxSize: Long,
    val diskCacheSize: Long,
    val diskCacheMaxSize: Long,
    val memoryHitCount: Long,
    val memoryMissCount: Long,
    val diskHitCount: Long,
    val diskMissCount: Long
) {
    val memoryHitRate: Float
        get() = if (memoryHitCount + memoryMissCount > 0) {
            memoryHitCount.toFloat() / (memoryHitCount + memoryMissCount)
        } else 0f
    
    val diskHitRate: Float
        get() = if (diskHitCount + diskMissCount > 0) {
            diskHitCount.toFloat() / (diskHitCount + diskMissCount)
        } else 0f
    
    val totalHitRate: Float
        get() = if (memoryHitCount + memoryMissCount + diskHitCount + diskMissCount > 0) {
            (memoryHitCount + diskHitCount).toFloat() / 
            (memoryHitCount + memoryMissCount + diskHitCount + diskMissCount)
        } else 0f
}
