package com.xichen.matelink.core.ui.image

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 图片缓存管理器
 * 基于Coil的缓存管理和优化
 */
@Singleton
class ImageCacheManager @Inject constructor(
    private val context: Context,
    private val imageLoaderManager: ImageLoaderManager
) {
    
    /**
     * 获取图片缓存统计信息
     */
    fun getCacheStats(): ImageCacheStatistics {
        val defaultLoader = imageLoaderManager.defaultImageLoader
        val avatarLoader = imageLoaderManager.avatarImageLoader
        val largeLoader = imageLoaderManager.largeImageLoader
        val thumbnailLoader = imageLoaderManager.thumbnailImageLoader
        
        return ImageCacheStatistics(
            defaultCache = getCacheInfo(defaultLoader),
            avatarCache = getCacheInfo(avatarLoader),
            largeImageCache = getCacheInfo(largeLoader),
            thumbnailCache = getCacheInfo(thumbnailLoader)
        )
    }
    
    /**
     * 获取单个ImageLoader的缓存信息
     */
    private fun getCacheInfo(imageLoader: ImageLoader): CacheInfo {
        val memoryCache = imageLoader.memoryCache
        val diskCache = imageLoader.diskCache
        
        return CacheInfo(
            memorySize = memoryCache?.size ?: 0,
            memoryMaxSize = memoryCache?.maxSize ?: 0,
            diskSize = diskCache?.size ?: 0,
            diskMaxSize = diskCache?.maxSize ?: 0
        )
    }
    
    /**
     * 清理所有图片缓存
     */
    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        imageLoaderManager.clearAllImageCache()
    }
    
    /**
     * 清理内存缓存
     */
    fun clearMemoryCache() {
        imageLoaderManager.defaultImageLoader.memoryCache?.clear()
        imageLoaderManager.avatarImageLoader.memoryCache?.clear()
        imageLoaderManager.largeImageLoader.memoryCache?.clear()
        imageLoaderManager.thumbnailImageLoader.memoryCache?.clear()
    }
    
    /**
     * 清理磁盘缓存
     */
    suspend fun clearDiskCache() = withContext(Dispatchers.IO) {
        imageLoaderManager.defaultImageLoader.diskCache?.clear()
        imageLoaderManager.avatarImageLoader.diskCache?.clear()
        imageLoaderManager.largeImageLoader.diskCache?.clear()
        imageLoaderManager.thumbnailImageLoader.diskCache?.clear()
    }
    
    /**
     * 根据内存压力清理缓存
     */
    suspend fun cleanupByMemoryPressure(memoryStatus: MemoryStatus) {
        when (memoryStatus) {
            MemoryStatus.CRITICAL -> {
                // 危险状态：清理所有缓存
                clearAllCache()
            }
            MemoryStatus.HIGH -> {
                // 高使用率：清理大图缓存
                imageLoaderManager.largeImageLoader.memoryCache?.clear()
                imageLoaderManager.thumbnailImageLoader.diskCache?.clear()
            }
            MemoryStatus.MODERATE -> {
                // 中等使用率：清理缩略图内存缓存
                imageLoaderManager.thumbnailImageLoader.memoryCache?.clear()
            }
            MemoryStatus.GOOD -> {
                // 良好状态：无需清理
            }
        }
    }
    
    /**
     * 预加载图片
     */
    suspend fun preloadImage(
        imageUrl: String,
        size: coil.size.Size = coil.size.Size.ORIGINAL
    ) = withContext(Dispatchers.IO) {
        val request = coil.request.ImageRequest.Builder(context)
            .data(imageUrl)
            .size(size)
            .build()
        
        imageLoaderManager.defaultImageLoader.execute(request)
    }
    
    /**
     * 批量预加载图片
     */
    suspend fun preloadImages(
        imageUrls: List<String>,
        maxConcurrent: Int = 3
    ) = withContext(Dispatchers.IO) {
        imageUrls.chunked(maxConcurrent).forEach { chunk ->
            chunk.map { url ->
                kotlinx.coroutines.async {
                    preloadImage(url, coil.size.Size(300, 300))
                }
            }.forEach { it.await() }
        }
    }
    
    /**
     * 获取图片文件大小
     */
    suspend fun getImageFileSize(imageUrl: String): Long = withContext(Dispatchers.IO) {
        try {
            val diskCache = imageLoaderManager.defaultImageLoader.diskCache
            diskCache?.openSnapshot(imageUrl)?.use { snapshot ->
                snapshot.data.size
            } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * 检查图片是否已缓存
     */
    fun isImageCached(imageUrl: String): Boolean {
        val memoryCache = imageLoaderManager.defaultImageLoader.memoryCache
        return memoryCache?.get(coil.memory.MemoryCache.Key(imageUrl)) != null
    }
    
    /**
     * 获取缓存大小（格式化字符串）
     */
    fun getFormattedCacheSize(): String {
        val stats = getCacheStats()
        val totalBytes = stats.getTotalSize()
        return formatBytes(totalBytes)
    }
    
    /**
     * 格式化字节大小
     */
    private fun formatBytes(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        
        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$bytes B"
        }
    }
}

/**
 * 缓存信息
 */
data class CacheInfo(
    val memorySize: Long,       // 内存缓存大小
    val memoryMaxSize: Long,    // 内存缓存最大大小
    val diskSize: Long,         // 磁盘缓存大小
    val diskMaxSize: Long       // 磁盘缓存最大大小
) {
    val memoryUsagePercent: Float
        get() = if (memoryMaxSize > 0) (memorySize.toFloat() / memoryMaxSize) * 100 else 0f
    
    val diskUsagePercent: Float
        get() = if (diskMaxSize > 0) (diskSize.toFloat() / diskMaxSize) * 100 else 0f
}

/**
 * 图片缓存统计信息
 */
data class ImageCacheStatistics(
    val defaultCache: CacheInfo,
    val avatarCache: CacheInfo,
    val largeImageCache: CacheInfo,
    val thumbnailCache: CacheInfo
) {
    fun getTotalSize(): Long {
        return defaultCache.memorySize + defaultCache.diskSize +
               avatarCache.memorySize + avatarCache.diskSize +
               largeImageCache.memorySize + largeImageCache.diskSize +
               thumbnailCache.memorySize + thumbnailCache.diskSize
    }
    
    fun getTotalMemorySize(): Long {
        return defaultCache.memorySize + avatarCache.memorySize +
               largeImageCache.memorySize + thumbnailCache.memorySize
    }
    
    fun getTotalDiskSize(): Long {
        return defaultCache.diskSize + avatarCache.diskSize +
               largeImageCache.diskSize + thumbnailCache.diskSize
    }
}
