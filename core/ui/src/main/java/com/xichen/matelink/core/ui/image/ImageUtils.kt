package com.xichen.matelink.core.ui.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 图片处理工具类
 * 提供图片处理、转换、压缩等功能
 */

@Singleton
class ImageUtils @Inject constructor() {
    
    /**
     * 创建圆形图片
     */
    fun createCircleBitmap(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
        }
        
        val rect = Rect(0, 0, size, size)
        val rectF = RectF(rect)
        
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        
        return output
    }
    
    /**
     * 创建圆角图片
     */
    fun createRoundedBitmap(bitmap: Bitmap, radius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
        }
        
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        
        canvas.drawRoundRect(rectF, radius, radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        
        return output
    }
    
    /**
     * 压缩图片
     */
    fun compressBitmap(bitmap: Bitmap, quality: Int = 80): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }
    
    /**
     * 调整图片大小
     */
    fun resizeBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * 保存图片到文件
     */
    fun saveBitmapToFile(bitmap: Bitmap, file: File, quality: Int = 90): Boolean {
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 从文件加载图片
     */
    fun loadBitmapFromFile(file: File): Bitmap? {
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 从Uri加载图片
     */
    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 创建Coil图片请求
     */
    fun createImageRequest(
        context: Context,
        data: Any,
        size: Size = Size.ORIGINAL,
        scale: Scale = Scale.FIT
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(data)
            .size(size)
            .scale(scale)
            .build()
    }
    
    /**
     * 创建圆形图片请求
     */
    fun createCircleImageRequest(
        context: Context,
        data: Any,
        size: Size = Size.ORIGINAL
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(data)
            .size(size)
            .transformations(CircleCropTransformation())
            .build()
    }
    
    /**
     * 创建圆角图片请求
     */
    fun createRoundedImageRequest(
        context: Context,
        data: Any,
        radius: Dp = 8.dp,
        size: Size = Size.ORIGINAL
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(data)
            .size(size)
            .transformations(RoundedCornersTransformation(radius.value))
            .build()
    }
    
    /**
     * 创建模糊图片请求
     * 注意：Coil的BlurTransformation需要额外依赖
     */
    fun createBlurImageRequest(
        context: Context,
        data: Any,
        radius: Float = 10f,
        size: Size = Size.ORIGINAL
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(data)
            .size(size)
            .build()
    }
    
    /**
     * 创建灰度图片请求
     * 注意：Coil的GrayscaleTransformation需要额外依赖
     */
    fun createGrayscaleImageRequest(
        context: Context,
        data: Any,
        size: Size = Size.ORIGINAL
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(data)
            .size(size)
            .build()
    }
    
    /**
     * 将Bitmap转换为ImageBitmap
     */
    fun bitmapToImageBitmap(bitmap: Bitmap): ImageBitmap {
        return bitmap.asImageBitmap()
    }
    
    /**
     * 获取图片的宽高比
     */
    fun getAspectRatio(bitmap: Bitmap): Float {
        return bitmap.width.toFloat() / bitmap.height.toFloat()
    }
    
    /**
     * 检查图片是否为正方形
     */
    fun isSquare(bitmap: Bitmap): Boolean {
        return bitmap.width == bitmap.height
    }
    
    /**
     * 检查图片是否为横向
     */
    fun isLandscape(bitmap: Bitmap): Boolean {
        return bitmap.width > bitmap.height
    }
    
    /**
     * 检查图片是否为纵向
     */
    fun isPortrait(bitmap: Bitmap): Boolean {
        return bitmap.height > bitmap.width
    }
    
    /**
     * 创建占位符图片
     */
    fun createPlaceholderBitmap(width: Int, height: Int, color: Int = Color.GRAY): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)
        return bitmap
    }
    
    /**
     * 创建错误图片
     */
    fun createErrorBitmap(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.RED
            textSize = 24f
            isAntiAlias = true
        }
        
        canvas.drawColor(Color.LTGRAY)
        canvas.drawText("Error", width / 2f - 30f, height / 2f, paint)
        
        return bitmap
    }
}
