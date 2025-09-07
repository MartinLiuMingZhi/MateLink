package com.xichen.matelink.core.common.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import java.io.File
import java.text.DecimalFormat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 内存管理工具类
 * 提供内存监控、优化和管理功能
 */
@Singleton
class MemoryUtils @Inject constructor(
    private val context: Context
) {
    
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val decimalFormat = DecimalFormat("#.##")
    
    /**
     * 获取应用内存信息
     */
    fun getAppMemoryInfo(): AppMemoryInfo {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        
        // 使用反射安全访问内存属性
        val nativeHeap = try {
            val field = memoryInfo.javaClass.getDeclaredField("nativeHeap")
            field.isAccessible = true
            field.getInt(memoryInfo)
        } catch (e: Exception) {
            0
        }

        val dalvikHeap = try {
            val field = memoryInfo.javaClass.getDeclaredField("dalvikHeap")
            field.isAccessible = true
            field.getInt(memoryInfo)
        } catch (e: Exception) {
            0
        }

        return AppMemoryInfo(
            totalPss = memoryInfo.totalPss,
            totalPrivateDirty = memoryInfo.totalPrivateDirty,
            totalSharedDirty = memoryInfo.totalSharedDirty,
            nativeHeap = nativeHeap,
            dalvikHeap = dalvikHeap,
            otherPss = memoryInfo.otherPss
        )
    }
    
    /**
     * 获取系统内存信息
     */
    fun getSystemMemoryInfo(): SystemMemoryInfo {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        return SystemMemoryInfo(
            availMem = memoryInfo.availMem,
            totalMem = memoryInfo.totalMem,
            threshold = memoryInfo.threshold,
            lowMemory = memoryInfo.lowMemory
        )
    }
    
    /**
     * 获取堆内存信息
     */
    fun getHeapMemoryInfo(): HeapMemoryInfo {
        val runtime = Runtime.getRuntime()
        
        return HeapMemoryInfo(
            maxMemory = runtime.maxMemory(),
            totalMemory = runtime.totalMemory(),
            freeMemory = runtime.freeMemory(),
            usedMemory = runtime.totalMemory() - runtime.freeMemory()
        )
    }
    
    /**
     * 格式化内存大小
     */
    fun formatMemorySize(bytes: Long): String {
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
    
    /**
     * 检查内存使用率
     */
    fun getMemoryUsagePercentage(): Float {
        val heapInfo = getHeapMemoryInfo()
        return (heapInfo.usedMemory.toFloat() / heapInfo.maxMemory.toFloat()) * 100
    }
    
    /**
     * 检查是否内存不足
     */
    fun isLowMemory(): Boolean {
        val systemInfo = getSystemMemoryInfo()
        return systemInfo.lowMemory || getMemoryUsagePercentage() > 85f
    }
    
    /**
     * 获取内存状态
     */
    fun getMemoryStatus(): MemoryStatus {
        val usagePercentage = getMemoryUsagePercentage()
        
        return when {
            usagePercentage < 50f -> MemoryStatus.GOOD
            usagePercentage < 75f -> MemoryStatus.MODERATE
            usagePercentage < 90f -> MemoryStatus.HIGH
            else -> MemoryStatus.CRITICAL
        }
    }
    
    /**
     * 建议的内存操作
     */
    fun getMemoryRecommendation(): MemoryRecommendation {
        val status = getMemoryStatus()
        val systemInfo = getSystemMemoryInfo()
        
        return when {
            status == MemoryStatus.CRITICAL -> MemoryRecommendation.FORCE_CLEANUP
            status == MemoryStatus.HIGH -> MemoryRecommendation.CLEANUP_CACHE
            systemInfo.lowMemory -> MemoryRecommendation.REDUCE_QUALITY
            else -> MemoryRecommendation.NORMAL
        }
    }
    
    /**
     * 执行内存清理
     */
    fun performMemoryCleanup() {
        // 建议垃圾回收
        System.gc()
        
        // 清理图片缓存
        clearImageCache()
        
        // 清理临时数据
        clearTempData()
    }
    
    /**
     * 清理图片缓存
     */
    private fun clearImageCache() {
        // 通知图片加载库清理缓存
        // Coil.imageLoader(context).memoryCache?.clear()
    }
    
    /**
     * 清理临时数据
     */
    private fun clearTempData() {
        // 清理临时文件
        val tempDir = File(context.cacheDir, "temp")
        if (tempDir.exists()) {
            tempDir.deleteRecursively()
        }
    }
    
    /**
     * 获取应用内存限制
     */
    fun getMemoryLimit(): Long {
        return activityManager.memoryClass * 1024 * 1024L // MB转换为字节
    }
    
    /**
     * 获取大内存限制（如果应用声明了largeHeap）
     */
    fun getLargeMemoryLimit(): Long {
        return activityManager.largeMemoryClass * 1024 * 1024L
    }
    
    /**
     * 检查是否可以使用大内存
     */
    fun canUseLargeHeap(): Boolean {
        val applicationInfo = context.applicationInfo
        return (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP) != 0
    }
}

/**
 * 应用内存信息
 */
data class AppMemoryInfo(
    val totalPss: Int,           // 总PSS内存
    val totalPrivateDirty: Int,  // 私有脏内存
    val totalSharedDirty: Int,   // 共享脏内存
    val nativeHeap: Int,         // Native堆内存
    val dalvikHeap: Int,         // Dalvik堆内存
    val otherPss: Int            // 其他PSS内存
)

/**
 * 系统内存信息
 */
data class SystemMemoryInfo(
    val availMem: Long,          // 可用内存
    val totalMem: Long,          // 总内存
    val threshold: Long,         // 内存阈值
    val lowMemory: Boolean       // 是否内存不足
)

/**
 * 堆内存信息
 */
data class HeapMemoryInfo(
    val maxMemory: Long,         // 最大可用内存
    val totalMemory: Long,       // 总分配内存
    val freeMemory: Long,        // 空闲内存
    val usedMemory: Long         // 已使用内存
)

/**
 * 内存状态枚举
 */
enum class MemoryStatus {
    GOOD,           // 良好（< 50%）
    MODERATE,       // 中等（50-75%）
    HIGH,           // 较高（75-90%）
    CRITICAL        // 危险（> 90%）
}

/**
 * 内存建议枚举
 */
enum class MemoryRecommendation {
    NORMAL,         // 正常运行
    REDUCE_QUALITY, // 降低质量
    CLEANUP_CACHE,  // 清理缓存
    FORCE_CLEANUP   // 强制清理
}
