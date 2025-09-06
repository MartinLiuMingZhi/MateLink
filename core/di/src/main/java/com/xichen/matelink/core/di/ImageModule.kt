package com.xichen.matelink.core.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.xichen.matelink.core.common.utils.MemoryUtils
import com.xichen.matelink.core.ui.image.ImageCacheManager
import com.xichen.matelink.core.ui.image.ImageLoaderManager
import com.xichen.matelink.core.ui.image.ImageUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * 图片相关依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object ImageModule {
    
    /**
     * 提供默认ImageLoader
     */
    @Provides
    @Singleton
    @Named("default")
    fun provideDefaultImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.20)
                    .strongReferencesEnabled(false)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100 * 1024 * 1024) // 100MB
                    .build()
            }
            .respectCacheHeaders(false)
            .allowHardware(true)
            .logger(if (BuildConfig.DEBUG) DebugLogger() else null)
            .build()
    }
    
    /**
     * 提供头像专用ImageLoader
     */
    @Provides
    @Singleton
    @Named("avatar")
    fun provideAvatarImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.10)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("avatar_cache"))
                    .maxSizeBytes(20 * 1024 * 1024) // 20MB
                    .build()
            }
            .allowHardware(false)
            .build()
    }
    
    /**
     * 提供大图专用ImageLoader
     */
    @Provides
    @Singleton
    @Named("large")
    fun provideLargeImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.15)
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
     * 提供缩略图专用ImageLoader
     */
    @Provides
    @Singleton
    @Named("thumbnail")
    fun provideThumbnailImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.05)
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
     * 提供ImageLoaderManager
     */
    @Provides
    @Singleton
    fun provideImageLoaderManager(
        @ApplicationContext context: Context,
        memoryUtils: MemoryUtils
    ): ImageLoaderManager {
        return ImageLoaderManager(context, memoryUtils)
    }
    
    /**
     * 提供ImageCacheManager
     */
    @Provides
    @Singleton
    fun provideImageCacheManager(
        @ApplicationContext context: Context,
        imageLoaderManager: ImageLoaderManager
    ): ImageCacheManager {
        return ImageCacheManager(context, imageLoaderManager)
    }
    
    /**
     * 提供ImageUtils
     */
    @Provides
    @Singleton
    fun provideImageUtils(
        @ApplicationContext context: Context,
        imageLoaderManager: ImageLoaderManager
    ): ImageUtils {
        return ImageUtils(context, imageLoaderManager)
    }
}
