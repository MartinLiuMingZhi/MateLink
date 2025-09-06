package com.xichen.matelink.core.ui.image

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.xichen.matelink.core.common.utils.MemoryUtils
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MateLink 图片加载器管理
 * 基于Coil的自定义配置
 */
@Singleton
class ImageLoaderManager @Inject constructor(
    private val context: Context,
    private val memoryUtils: MemoryUtils
) {
    
    /**
     * 主图片加载器 - 用于一般图片加载
     */
    val defaultImageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.20) // 使用20%内存
                    .strongReferencesEnabled(false) // 允许GC回收
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100 * 1024 * 1024) // 100MB
                    .build()
            }
            .respectCacheHeaders(false)
            .allowHardware(true) // 启用硬件位图
            .logger(if (BuildConfig.DEBUG) DebugLogger() else null)
            .build()
    }
    
    /**
     * 头像专用加载器 - 优化小图片加载
     */
    val avatarImageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.10) // 使用10%内存
                    .strongReferencesEnabled(true) // 头像常驻内存
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("avatar_cache"))
                    .maxSizeBytes(20 * 1024 * 1024) // 20MB
                    .build()
            }
            .allowHardware(false) // 头像可能需要变换，不用硬件位图
            .build()
    }
    
    /**
     * 大图专用加载器 - 用于朋友圈大图
     */
    val largeImageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.15) // 使用15%内存
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("large_image_cache"))
                    .maxSizeBytes(200 * 1024 * 1024) // 200MB
                    .build()
            }
            .allowHardware(true)
            .build()
    }
    
    /**
     * 缩略图加载器 - 用于消息列表缩略图
     */
    val thumbnailImageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.05) // 使用5%内存
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("thumbnail_cache"))
                    .maxSizeBytes(30 * 1024 * 1024) // 30MB
                    .build()
            }
            .allowHardware(true)
            .build()
    }
    
    /**
     * 根据内存状态获取推荐的图片加载器
     */
    fun getRecommendedImageLoader(): ImageLoader {
        return when (memoryUtils.getMemoryStatus()) {
            MemoryStatus.GOOD -> defaultImageLoader
            MemoryStatus.MODERATE -> avatarImageLoader
            MemoryStatus.HIGH -> thumbnailImageLoader
            MemoryStatus.CRITICAL -> thumbnailImageLoader
        }
    }
    
    /**
     * 清理所有图片缓存
     */
    suspend fun clearAllImageCache() {
        defaultImageLoader.memoryCache?.clear()
        avatarImageLoader.memoryCache?.clear()
        largeImageLoader.memoryCache?.clear()
        thumbnailImageLoader.memoryCache?.clear()
        
        defaultImageLoader.diskCache?.clear()
        avatarImageLoader.diskCache?.clear()
        largeImageLoader.diskCache?.clear()
        thumbnailImageLoader.diskCache?.clear()
    }
    
    /**
     * 获取缓存统计信息
     */
    fun getCacheStats(): ImageCacheStats {
        val memoryCache = defaultImageLoader.memoryCache
        val diskCache = defaultImageLoader.diskCache
        
        return ImageCacheStats(
            memoryCacheSize = memoryCache?.size ?: 0,
            memoryMaxSize = memoryCache?.maxSize ?: 0,
            diskCacheSize = diskCache?.size ?: 0,
            diskMaxSize = diskCache?.maxSize ?: 0
        )
    }
}

/**
 * 图片缓存统计信息
 */
data class ImageCacheStats(
    val memoryCacheSize: Long,      // 内存缓存当前大小
    val memoryMaxSize: Long,        // 内存缓存最大大小
    val diskCacheSize: Long,        // 磁盘缓存当前大小
    val diskMaxSize: Long           // 磁盘缓存最大大小
)

/**
 * 图片加载配置
 */
object ImageConfig {
    // 缓存配置
    const val MEMORY_CACHE_PERCENT = 0.20f      // 内存缓存占比
    const val DISK_CACHE_SIZE = 100L * 1024 * 1024  // 磁盘缓存大小
    
    // 图片尺寸配置
    const val AVATAR_SIZE = 200                  // 头像最大尺寸
    const val THUMBNAIL_SIZE = 300               // 缩略图最大尺寸
    const val LARGE_IMAGE_SIZE = 1080            // 大图最大尺寸
    
    // 质量配置
    const val THUMBNAIL_QUALITY = 60            // 缩略图质量
    const val NORMAL_QUALITY = 80               // 普通图片质量
    const val HIGH_QUALITY = 90                 // 高质量图片
}
