package com.xichen.matelink.core.ui.image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 图片工具类
 * 提供图片处理、转换、保存等功能
 */
@Singleton
class ImageUtils @Inject constructor(
    private val context: Context,
    private val imageLoaderManager: ImageLoaderManager
) {
    
    /**
     * 创建圆形头像ImageRequest
     */
    @Composable
    fun createAvatarRequest(
        imageUrl: String?,
        size: Int = 200
    ): ImageRequest {
        return ImageRequest.Builder(LocalContext.current)
            .data(imageUrl ?: getDefaultAvatarResource())
            .size(size, size)
            .transformations(CircleCropTransformation())
            .crossfade(true)
            .build()
    }
    
    /**
     * 创建聊天图片ImageRequest
     */
    @Composable
    fun createChatImageRequest(
        imageUrl: String,
        maxSize: Int = 600,
        cornerRadius: Float = 8f
    ): ImageRequest {
        return ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(maxSize, maxSize)
            .transformations(RoundedCornersTransformation(cornerRadius))
            .crossfade(true)
            .build()
    }
    
    /**
     * 创建朋友圈图片ImageRequest
     */
    @Composable
    fun createMomentImageRequest(
        imageUrl: String,
        isGrid: Boolean = false
    ): ImageRequest {
        val size = if (isGrid) 300 else 800
        
        return ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(size, size)
            .transformations(RoundedCornersTransformation(4f))
            .crossfade(true)
            .build()
    }
    
    /**
     * 创建缩略图ImageRequest
     */
    @Composable
    fun createThumbnailRequest(
        imageUrl: String,
        size: Int = 150
    ): ImageRequest {
        return ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(size, size)
            .transformations(RoundedCornersTransformation(4f))
            .crossfade(false) // 缩略图不需要动画
            .build()
    }
    
    /**
     * 保存图片到本地
     */
    suspend fun saveImageToLocal(
        imageUrl: String,
        fileName: String? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .build()
            
            val result = imageLoaderManager.defaultImageLoader.execute(request)
            val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                ?: return@withContext Result.failure(Exception("无法获取图片"))
            
            val file = File(
                context.getExternalFilesDir("images"),
                fileName ?: "image_${System.currentTimeMillis()}.jpg"
            )
            
            file.parentFile?.mkdirs()
            
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取图片信息
     */
    suspend fun getImageInfo(imageUrl: String): ImageInfo? = withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .size(Size.ORIGINAL)
                .build()
            
            val result = imageLoaderManager.defaultImageLoader.execute(request)
            val drawable = result.drawable
            
            if (drawable != null) {
                ImageInfo(
                    width = drawable.intrinsicWidth,
                    height = drawable.intrinsicHeight,
                    size = getImageFileSize(imageUrl)
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 获取图片文件大小
     */
    private suspend fun getImageFileSize(imageUrl: String): Long {
        return try {
            val diskCache = imageLoaderManager.defaultImageLoader.diskCache
            diskCache?.openSnapshot(imageUrl)?.use { snapshot ->
                snapshot.data.size
            } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * 压缩图片
     */
    suspend fun compressImage(
        imageUrl: String,
        maxWidth: Int = 800,
        maxHeight: Int = 800,
        quality: Int = 80
    ): String? = withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .size(maxWidth, maxHeight)
                .build()
            
            val result = imageLoaderManager.defaultImageLoader.execute(request)
            val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                ?: return@withContext null
            
            val compressedFile = File(
                context.cacheDir,
                "compressed_${System.currentTimeMillis()}.jpg"
            )
            
            FileOutputStream(compressedFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            
            compressedFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 预热图片缓存
     */
    suspend fun warmupCache(imageUrls: List<String>) = withContext(Dispatchers.IO) {
        imageUrls.forEach { url ->
            try {
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .size(300, 300) // 预热缩略图
                    .build()
                
                imageLoaderManager.defaultImageLoader.execute(request)
            } catch (e: Exception) {
                // 忽略预热失败
            }
        }
    }
    
    /**
     * 清理过期缓存
     */
    suspend fun cleanupExpiredCache(maxAgeDays: Int = 7) = withContext(Dispatchers.IO) {
        try {
            // 清理磁盘缓存中的过期文件
            val cacheDir = File(context.cacheDir, "image_cache")
            if (cacheDir.exists()) {
                val cutoffTime = System.currentTimeMillis() - (maxAgeDays * 24 * 60 * 60 * 1000L)
                
                cacheDir.listFiles()?.forEach { file ->
                    if (file.lastModified() < cutoffTime) {
                        file.delete()
                    }
                }
            }
        } catch (e: Exception) {
            // 忽略清理失败
        }
    }
    
    /**
     * 获取默认头像资源
     */
    private fun getDefaultAvatarResource(): String {
        return "android.resource://com.xichen.matelink/drawable/default_avatar"
    }
    
    /**
     * 获取默认空间头像资源
     */
    fun getDefaultSpaceAvatarResource(): String {
        return "android.resource://com.xichen.matelink/drawable/default_space_avatar"
    }
    
    /**
     * 生成头像占位符
     */
    fun generateAvatarPlaceholder(name: String): String {
        // 可以根据用户名生成颜色头像
        // 这里返回默认资源
        return getDefaultAvatarResource()
    }
    
    /**
     * 检查图片URL是否有效
     */
    fun isValidImageUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        
        val imageExtensions = listOf("jpg", "jpeg", "png", "gif", "webp", "bmp")
        val lowerUrl = url.lowercase()
        
        return imageExtensions.any { ext ->
            lowerUrl.endsWith(".$ext") || lowerUrl.contains(".$ext?")
        } || lowerUrl.startsWith("http") || lowerUrl.startsWith("android.resource")
    }
    
    /**
     * 获取图片类型
     */
    fun getImageType(url: String): ImageType {
        val lowerUrl = url.lowercase()
        
        return when {
            lowerUrl.endsWith(".gif") -> ImageType.GIF
            lowerUrl.endsWith(".svg") -> ImageType.SVG
            lowerUrl.endsWith(".webp") -> ImageType.WEBP
            lowerUrl.endsWith(".png") -> ImageType.PNG
            lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg") -> ImageType.JPEG
            else -> ImageType.UNKNOWN
        }
    }
}

/**
 * 图片信息
 */
data class ImageInfo(
    val width: Int,
    val height: Int,
    val size: Long
) {
    val aspectRatio: Float
        get() = if (height > 0) width.toFloat() / height.toFloat() else 1f
    
    val formattedSize: String
        get() {
            val kb = size / 1024.0
            val mb = kb / 1024.0
            return when {
                mb >= 1 -> String.format("%.2f MB", mb)
                kb >= 1 -> String.format("%.2f KB", kb)
                else -> "$size B"
            }
        }
}

/**
 * 图片类型枚举
 */
enum class ImageType {
    JPEG,
    PNG,
    GIF,
    WEBP,
    SVG,
    UNKNOWN
}

/**
 * 图片质量配置
 */
enum class ImageQuality(val size: Int, val quality: Int) {
    LOW(300, 60),       // 低质量
    MEDIUM(600, 75),    // 中等质量
    HIGH(1080, 90),     // 高质量
    ORIGINAL(-1, 100)   // 原始质量
}

/**
 * 图片变换工具
 */
object ImageTransforms {
    /**
     * 圆形裁剪
     */
    fun circle() = CircleCropTransformation()
    
    /**
     * 圆角裁剪
     */
    fun roundedCorners(radius: Float) = RoundedCornersTransformation(radius)
    
    /**
     * 模糊效果
     */
    fun blur(radius: Float = 10f, sampling: Float = 1f) = BlurTransformation(context, radius, sampling)
    
    /**
     * 灰度效果
     */
    fun grayscale() = GrayscaleTransformation()
    
    /**
     * 组合变换
     */
    fun combined(vararg transformations: Transformation) = transformations.toList()
}
