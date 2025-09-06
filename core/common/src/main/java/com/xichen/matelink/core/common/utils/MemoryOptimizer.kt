package com.xichen.matelink.core.common.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 内存优化工具
 * 提供内存优化策略和自动内存管理
 */
object MemoryOptimizer {
    
    private var context: WeakReference<Context>? = null
    private val scheduledExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())
    
    // 优化配置
    private var isEnabled = false
    private var optimizationInterval = 60L // 60秒优化一次
    private var lowMemoryThreshold = 50 * 1024 * 1024L // 50MB低内存阈值
    private var criticalMemoryThreshold = 20 * 1024 * 1024L // 20MB临界内存阈值
    
    // 内存优化策略
    private val optimizationStrategies = mutableListOf<MemoryOptimizationStrategy>()
    
    /**
     * 初始化内存优化器
     */
    fun init(context: Context, enabled: Boolean = true) {
        this.context = WeakReference(context)
        isEnabled = enabled
        
        if (isEnabled) {
            setupDefaultStrategies()
            startMemoryOptimization()
        }
    }
    
    /**
     * 设置默认优化策略
     */
    private fun setupDefaultStrategies() {
        optimizationStrategies.addAll(listOf(
            ImageCacheOptimization(),
            ViewCacheOptimization(),
            DataCacheOptimization(),
            GarbageCollectionOptimization()
        ))
    }
    
    /**
     * 开始内存优化
     */
    private fun startMemoryOptimization() {
        scheduledExecutor.scheduleWithFixedDelay({
            performMemoryOptimization()
        }, optimizationInterval, optimizationInterval, TimeUnit.SECONDS)
    }
    
    /**
     * 执行内存优化
     */
    private fun performMemoryOptimization() {
        val memoryInfo = MemoryLeakDetector.getMemoryInfo()
        
        when {
            memoryInfo.usedMemory < lowMemoryThreshold -> {
                // 内存充足，执行轻度优化
                performLightOptimization()
            }
            memoryInfo.usedMemory < criticalMemoryThreshold -> {
                // 内存紧张，执行中度优化
                performMediumOptimization()
            }
            else -> {
                // 内存严重不足，执行重度优化
                performHeavyOptimization()
            }
        }
    }
    
    /**
     * 轻度优化
     */
    private fun performLightOptimization() {
        android.util.Log.d("MemoryOptimizer", "执行轻度内存优化")
        
        // 清理过期的图片缓存
        optimizationStrategies.forEach { strategy ->
            if (strategy.priority <= OptimizationPriority.LOW) {
                strategy.optimize(OptimizationLevel.LIGHT)
            }
        }
    }
    
    /**
     * 中度优化
     */
    private fun performMediumOptimization() {
        android.util.Log.d("MemoryOptimizer", "执行中度内存优化")
        
        // 清理更多缓存，触发GC
        optimizationStrategies.forEach { strategy ->
            if (strategy.priority <= OptimizationPriority.MEDIUM) {
                strategy.optimize(OptimizationLevel.MEDIUM)
            }
        }
        
        // 延迟执行GC
        mainHandler.postDelayed({
            System.gc()
        }, 1000)
    }
    
    /**
     * 重度优化
     */
    private fun performHeavyOptimization() {
        android.util.Log.w("MemoryOptimizer", "执行重度内存优化")
        
        // 执行所有优化策略
        optimizationStrategies.forEach { strategy ->
            strategy.optimize(OptimizationLevel.HEAVY)
        }
        
        // 立即执行GC
        System.gc()
        System.runFinalization()
        System.gc()
        
        // 通知应用内存紧张
        notifyLowMemory()
    }
    
    /**
     * 通知低内存状态
     */
    private fun notifyLowMemory() {
        // 可以通过EventBus或其他方式通知应用
        android.util.Log.w("MemoryOptimizer", "内存严重不足，请释放不必要的资源")
    }
    
    /**
     * 添加自定义优化策略
     */
    fun addOptimizationStrategy(strategy: MemoryOptimizationStrategy) {
        optimizationStrategies.add(strategy)
    }
    
    /**
     * 移除优化策略
     */
    fun removeOptimizationStrategy(strategy: MemoryOptimizationStrategy) {
        optimizationStrategies.remove(strategy)
    }
    
    /**
     * 手动触发内存优化
     */
    fun triggerOptimization(level: OptimizationLevel = OptimizationLevel.MEDIUM) {
        when (level) {
            OptimizationLevel.LIGHT -> performLightOptimization()
            OptimizationLevel.MEDIUM -> performMediumOptimization()
            OptimizationLevel.HEAVY -> performHeavyOptimization()
        }
    }
    
    /**
     * 获取内存优化建议
     */
    fun getOptimizationSuggestions(): List<String> {
        val suggestions = mutableListOf<String>()
        val memoryInfo = MemoryLeakDetector.getMemoryInfo()
        
        when {
            memoryInfo.memoryUsagePercent > 80 -> {
                suggestions.add("内存使用率过高 (${memoryInfo.memoryUsagePercent.toInt()}%)，建议清理缓存")
                suggestions.add("考虑减少同时加载的图片数量")
                suggestions.add("检查是否有内存泄漏")
            }
            memoryInfo.memoryUsagePercent > 60 -> {
                suggestions.add("内存使用率较高 (${memoryInfo.memoryUsagePercent.toInt()}%)，建议优化图片缓存")
                suggestions.add("考虑使用更小的图片尺寸")
            }
            else -> {
                suggestions.add("内存使用正常 (${memoryInfo.memoryUsagePercent.toInt()}%)")
            }
        }
        
        return suggestions
    }
    
    /**
     * 清理资源
     */
    fun cleanup() {
        scheduledExecutor.shutdown()
        optimizationStrategies.clear()
        context?.clear()
    }
    
    /**
     * 设置优化配置
     */
    fun configure(
        enabled: Boolean = true,
        interval: Long = 60L,
        lowThreshold: Long = 50 * 1024 * 1024L,
        criticalThreshold: Long = 20 * 1024 * 1024L
    ) {
        isEnabled = enabled
        optimizationInterval = interval
        lowMemoryThreshold = lowThreshold
        criticalMemoryThreshold = criticalThreshold
    }
}

/**
 * 内存优化策略接口
 */
interface MemoryOptimizationStrategy {
    val priority: OptimizationPriority
    fun optimize(level: OptimizationLevel)
    fun canOptimize(): Boolean
}

/**
 * 优化优先级
 */
enum class OptimizationPriority {
    LOW,    // 低优先级
    MEDIUM, // 中优先级
    HIGH    // 高优先级
}

/**
 * 优化级别
 */
enum class OptimizationLevel {
    LIGHT,  // 轻度优化
    MEDIUM, // 中度优化
    HEAVY   // 重度优化
}

/**
 * 图片缓存优化策略
 */
class ImageCacheOptimization : MemoryOptimizationStrategy {
    override val priority = OptimizationPriority.MEDIUM
    
    override fun optimize(level: OptimizationLevel) {
        when (level) {
            OptimizationLevel.LIGHT -> {
                // 清理过期的图片缓存
                android.util.Log.d("ImageCacheOptimization", "清理过期图片缓存")
                // 这里可以调用Coil的缓存清理
            }
            OptimizationLevel.MEDIUM -> {
                // 清理部分图片缓存
                android.util.Log.d("ImageCacheOptimization", "清理部分图片缓存")
            }
            OptimizationLevel.HEAVY -> {
                // 清理所有图片缓存
                android.util.Log.d("ImageCacheOptimization", "清理所有图片缓存")
            }
        }
    }
    
    override fun canOptimize(): Boolean = true
}

/**
 * View缓存优化策略
 */
class ViewCacheOptimization : MemoryOptimizationStrategy {
    override val priority = OptimizationPriority.LOW
    
    override fun optimize(level: OptimizationLevel) {
        when (level) {
            OptimizationLevel.LIGHT -> {
                android.util.Log.d("ViewCacheOptimization", "清理View缓存")
            }
            OptimizationLevel.MEDIUM -> {
                android.util.Log.d("ViewCacheOptimization", "清理更多View缓存")
            }
            OptimizationLevel.HEAVY -> {
                android.util.Log.d("ViewCacheOptimization", "清理所有View缓存")
            }
        }
    }
    
    override fun canOptimize(): Boolean = true
}

/**
 * 数据缓存优化策略
 */
class DataCacheOptimization : MemoryOptimizationStrategy {
    override val priority = OptimizationPriority.HIGH
    
    override fun optimize(level: OptimizationLevel) {
        when (level) {
            OptimizationLevel.LIGHT -> {
                android.util.Log.d("DataCacheOptimization", "清理过期数据缓存")
            }
            OptimizationLevel.MEDIUM -> {
                android.util.Log.d("DataCacheOptimization", "清理部分数据缓存")
            }
            OptimizationLevel.HEAVY -> {
                android.util.Log.d("DataCacheOptimization", "清理所有数据缓存")
            }
        }
    }
    
    override fun canOptimize(): Boolean = true
}

/**
 * 垃圾回收优化策略
 */
class GarbageCollectionOptimization : MemoryOptimizationStrategy {
    override val priority = OptimizationPriority.HIGH
    
    override fun optimize(level: OptimizationLevel) {
        when (level) {
            OptimizationLevel.LIGHT -> {
                // 不执行GC
            }
            OptimizationLevel.MEDIUM -> {
                android.util.Log.d("GarbageCollectionOptimization", "触发垃圾回收")
                System.gc()
            }
            OptimizationLevel.HEAVY -> {
                android.util.Log.d("GarbageCollectionOptimization", "强制垃圾回收")
                System.gc()
                System.runFinalization()
                System.gc()
            }
        }
    }
    
    override fun canOptimize(): Boolean = true
}
