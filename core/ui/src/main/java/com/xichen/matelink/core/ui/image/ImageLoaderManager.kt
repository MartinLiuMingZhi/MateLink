package com.xichen.matelink.core.ui.image

import android.content.Context
import coil.ImageLoader
import coil.request.CachePolicy
import coil.decode.GifDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import com.xichen.matelink.core.common.utils.MemoryUtils
import com.xichen.matelink.core.common.utils.MemoryStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * 图片加载器管理器
 * 提供不同配置的ImageLoader实例
 */

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultImageLoader

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HighQualityImageLoader

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LowMemoryImageLoader

/**
 * 图片加载器管理器
 * 根据内存状态提供不同的ImageLoader配置
 */
@Singleton
class ImageLoaderManager @javax.inject.Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * 默认ImageLoader
     */
    @DefaultImageLoader 
    private val defaultImageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .components {
                add(GifDecoder.Factory())
                add(SvgDecoder.Factory())
                add(VideoFrameDecoder.Factory())
            }
            .respectCacheHeaders(false)
            .crossfade(true)
            .crossfade(300)
            .build()
    }
    
    /**
     * 高质量ImageLoader
     */
    @HighQualityImageLoader 
    private val highQualityImageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .components {
                add(GifDecoder.Factory())
                add(SvgDecoder.Factory())
                add(VideoFrameDecoder.Factory())
            }
            .respectCacheHeaders(false)
            .crossfade(true)
            .crossfade(300)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }
    
    /**
     * 低内存ImageLoader
     */
    @LowMemoryImageLoader 
    private val lowMemoryImageLoader: ImageLoader by lazy {
        val memoryUtils = MemoryUtils(context)
        val memoryStatus = memoryUtils.getMemoryStatus()
        
        ImageLoader.Builder(context)
            .components {
                add(GifDecoder.Factory())
                add(SvgDecoder.Factory())
                add(VideoFrameDecoder.Factory())
            }
            .respectCacheHeaders(false)
            .crossfade(false) // 低内存模式下关闭动画
            .memoryCachePolicy(
                when (memoryStatus) {
                    MemoryStatus.CRITICAL -> CachePolicy.DISABLED
                    MemoryStatus.HIGH -> CachePolicy.READ_ONLY
                    MemoryStatus.MODERATE -> CachePolicy.ENABLED
                    MemoryStatus.GOOD -> CachePolicy.ENABLED
                }
            )
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }
    
    /**
     * 根据内存状态获取合适的ImageLoader
     */
    fun getImageLoader(): ImageLoader {
        val memoryUtils = MemoryUtils(context)
        val memoryStatus = memoryUtils.getMemoryStatus()
        
        return when (memoryStatus) {
            MemoryStatus.CRITICAL, MemoryStatus.HIGH -> lowMemoryImageLoader
            MemoryStatus.MODERATE -> defaultImageLoader
            MemoryStatus.GOOD -> highQualityImageLoader
        }
    }
    
    /**
     * 获取默认ImageLoader
     */
    fun getDefaultLoader(): ImageLoader = defaultImageLoader
    
    /**
     * 获取高质量ImageLoader
     */
    fun getHighQualityLoader(): ImageLoader = highQualityImageLoader
    
    /**
     * 获取低内存ImageLoader
     */
    fun getLowMemoryLoader(): ImageLoader = lowMemoryImageLoader
}

// ImageLoaderModule temporarily removed to resolve Hilt dependency issues