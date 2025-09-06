package com.xichen.matelink

import android.app.Application
import com.xichen.matelink.core.common.utils.DensityAdapterManager
import com.xichen.matelink.core.common.utils.MemoryLeakDetector
import com.xichen.matelink.core.common.utils.MemoryOptimizer
import dagger.hilt.android.HiltAndroidApp

/**
 * MateLink应用程序入口
 */
@HiltAndroidApp
class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化屏幕适配
        initScreenAdapter()
        
        // 初始化内存管理
        initMemoryManagement()
    }
    
    /**
     * 初始化屏幕适配
     */
    private fun initScreenAdapter() {
        // 初始化密度适配管理器
        DensityAdapterManager.init(this)
    }
    
    /**
     * 初始化内存管理
     */
    private fun initMemoryManagement() {
        // 初始化内存泄漏检测
        MemoryLeakDetector.init(this, enabled = true)
        
        // 初始化内存优化器
        MemoryOptimizer.init(this, enabled = true)
    }
}