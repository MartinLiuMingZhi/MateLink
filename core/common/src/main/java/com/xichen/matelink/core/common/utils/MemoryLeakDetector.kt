package com.xichen.matelink.core.common.utils

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Debug
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 内存泄漏检测工具
 * 用于检测Activity、Fragment等组件的内存泄漏
 */
object MemoryLeakDetector {
    
    private val activityReferences = ConcurrentHashMap<String, WeakReference<Activity>>()
    private val fragmentReferences = ConcurrentHashMap<String, WeakReference<Any>>()
    private val scheduledExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())
    
    // 检测配置
    private var isEnabled = false
    private var checkInterval = 30L // 30秒检查一次
    private var maxMemoryThreshold = 100 * 1024 * 1024L // 100MB阈值
    
    /**
     * 初始化内存泄漏检测
     */
    fun init(application: Application, enabled: Boolean = true) {
        isEnabled = enabled
        if (isEnabled) {
            startMemoryMonitoring()
        }
    }
    
    /**
     * 注册Activity引用
     */
    fun registerActivity(activity: Activity) {
        if (!isEnabled) return
        
        val key = activity.javaClass.simpleName + "_" + System.identityHashCode(activity)
        activityReferences[key] = WeakReference(activity)
        
        // 5秒后检查是否还存在强引用
        mainHandler.postDelayed({
            checkActivityLeak(key)
        }, 5000)
    }
    
    /**
     * 注册Fragment引用
     */
    fun registerFragment(fragment: Any) {
        if (!isEnabled) return
        
        val key = fragment.javaClass.simpleName + "_" + System.identityHashCode(fragment)
        fragmentReferences[key] = WeakReference(fragment)
        
        // 5秒后检查是否还存在强引用
        mainHandler.postDelayed({
            checkFragmentLeak(key)
        }, 5000)
    }
    
    /**
     * 检查Activity内存泄漏
     */
    private fun checkActivityLeak(key: String) {
        val weakRef = activityReferences[key]
        if (weakRef?.get() == null) {
            // Activity已被回收，移除引用
            activityReferences.remove(key)
        } else {
            // Activity仍然存在，可能存在内存泄漏
            val activity = weakRef.get()
            if (activity != null && activity.isFinishing) {
                // Activity正在finish但仍被引用，记录泄漏
                logMemoryLeak("Activity", activity.javaClass.simpleName, key)
            }
        }
    }
    
    /**
     * 检查Fragment内存泄漏
     */
    private fun checkFragmentLeak(key: String) {
        val weakRef = fragmentReferences[key]
        if (weakRef?.get() == null) {
            // Fragment已被回收，移除引用
            fragmentReferences.remove(key)
        } else {
            // Fragment仍然存在，可能存在内存泄漏
            val fragment = weakRef.get()
            if (fragment != null) {
                // 检查Fragment是否已detach
                val isDetached = try {
                    val field = fragment.javaClass.getDeclaredField("mDetached")
                    field.isAccessible = true
                    field.getBoolean(fragment)
                } catch (e: Exception) {
                    false
                }
                
                if (isDetached) {
                    logMemoryLeak("Fragment", fragment.javaClass.simpleName, key)
                }
            }
        }
    }
    
    /**
     * 记录内存泄漏
     */
    private fun logMemoryLeak(type: String, className: String, key: String) {
        val memoryInfo = getMemoryInfo()
        val logMessage = """
            🚨 内存泄漏检测到 $type: $className
            Key: $key
            当前内存使用: ${memoryInfo.usedMemoryMB}MB
            可用内存: ${memoryInfo.availableMemoryMB}MB
            堆内存: ${memoryInfo.heapMemoryMB}MB
        """.trimIndent()
        
        android.util.Log.w("MemoryLeakDetector", logMessage)
        
        // 可以在这里添加上报逻辑
        reportMemoryLeak(type, className, key, memoryInfo)
    }
    
    /**
     * 开始内存监控
     */
    private fun startMemoryMonitoring() {
        scheduledExecutor.scheduleWithFixedDelay({
            checkMemoryUsage()
        }, checkInterval, checkInterval, TimeUnit.SECONDS)
    }
    
    /**
     * 检查内存使用情况
     */
    private fun checkMemoryUsage() {
        val memoryInfo = getMemoryInfo()
        
        if (memoryInfo.usedMemoryMB > maxMemoryThreshold / (1024 * 1024)) {
            android.util.Log.w("MemoryLeakDetector", 
                "内存使用过高: ${memoryInfo.usedMemoryMB}MB, 阈值: ${maxMemoryThreshold / (1024 * 1024)}MB")
            
            // 触发垃圾回收
            System.gc()
            
            // 再次检查
            val afterGcInfo = getMemoryInfo()
            android.util.Log.i("MemoryLeakDetector", 
                "GC后内存: ${afterGcInfo.usedMemoryMB}MB")
        }
    }
    
    /**
     * 获取内存信息
     */
    fun getMemoryInfo(): MemoryInfo {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        
        val memoryInfo = MemoryInfo(
            maxMemory = maxMemory,
            totalMemory = totalMemory,
            freeMemory = freeMemory,
            usedMemory = usedMemory
        )
        
        // 如果支持，获取更详细的内存信息
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val debugMemoryInfo = Debug.MemoryInfo()
            Debug.getMemoryInfo(debugMemoryInfo)
            memoryInfo.nativeHeapSize = debugMemoryInfo.nativeHeapSize
            memoryInfo.nativeHeapAllocated = debugMemoryInfo.nativeHeapAllocated
        }
        
        return memoryInfo
    }
    
    /**
     * 上报内存泄漏
     */
    private fun reportMemoryLeak(type: String, className: String, key: String, memoryInfo: MemoryInfo) {
        // 这里可以集成崩溃上报SDK，如Firebase Crashlytics
        // FirebaseCrashlytics.getInstance().log("Memory leak detected: $type - $className")
        
        // 或者保存到本地文件
        val leakInfo = """
            Time: ${System.currentTimeMillis()}
            Type: $type
            Class: $className
            Key: $key
            Memory: ${memoryInfo.usedMemoryMB}MB
        """.trimIndent()
        
        // 保存到文件
        try {
            val file = java.io.File(android.os.Environment.getExternalStorageDirectory(), "memory_leaks.txt")
            file.appendText(leakInfo + "\n\n")
        } catch (e: Exception) {
            android.util.Log.e("MemoryLeakDetector", "Failed to save leak info", e)
        }
    }
    
    /**
     * 清理资源
     */
    fun cleanup() {
        scheduledExecutor.shutdown()
        activityReferences.clear()
        fragmentReferences.clear()
    }
    
    /**
     * 设置检测配置
     */
    fun configure(
        enabled: Boolean = true,
        interval: Long = 30L,
        threshold: Long = 100 * 1024 * 1024L
    ) {
        isEnabled = enabled
        checkInterval = interval
        maxMemoryThreshold = threshold
    }
    
    /**
     * 强制垃圾回收
     */
    fun forceGarbageCollection() {
        System.gc()
        System.runFinalization()
        System.gc()
    }
    
    /**
     * 获取当前注册的引用数量
     */
    fun getReferenceCount(): ReferenceCount {
        return ReferenceCount(
            activityCount = activityReferences.size,
            fragmentCount = fragmentReferences.size
        )
    }
}

/**
 * 内存信息数据类
 */
data class MemoryInfo(
    val maxMemory: Long,
    val totalMemory: Long,
    val freeMemory: Long,
    val usedMemory: Long,
    var nativeHeapSize: Int = 0,
    var nativeHeapAllocated: Int = 0
) {
    val maxMemoryMB: Long get() = maxMemory / (1024 * 1024)
    val totalMemoryMB: Long get() = totalMemory / (1024 * 1024)
    val freeMemoryMB: Long get() = freeMemory / (1024 * 1024)
    val usedMemoryMB: Long get() = usedMemory / (1024 * 1024)
    val availableMemoryMB: Long get() = (maxMemory - usedMemory) / (1024 * 1024)
    val heapMemoryMB: Long get() = totalMemory / (1024 * 1024)
    val nativeHeapSizeMB: Int get() = nativeHeapSize / (1024 * 1024)
    val nativeHeapAllocatedMB: Int get() = nativeHeapAllocated / (1024 * 1024)
    
    val memoryUsagePercent: Float get() = (usedMemory.toFloat() / maxMemory * 100)
    val heapUsagePercent: Float get() = (usedMemory.toFloat() / totalMemory * 100)
}

/**
 * 引用计数数据类
 */
data class ReferenceCount(
    val activityCount: Int,
    val fragmentCount: Int
) {
    val totalCount: Int get() = activityCount + fragmentCount
}
