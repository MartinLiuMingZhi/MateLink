package com.xichen.matelink.core.common.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.util.DisplayMetrics
import java.util.*

/**
 * 今日头条屏幕适配方案
 * 基于修改系统密度值实现屏幕适配
 */
object DensityAdapterManager {
    
    // 设计稿宽度（dp）
    private const val DESIGN_WIDTH_IN_DP = 375f
    
    // 系统默认密度
    private var systemDensity = 0f
    private var systemDensityDpi = 0
    private var systemScaledDensity = 0f
    
    // 适配后的密度
    private var targetDensity = 0f
    private var targetDensityDpi = 0
    private var targetScaledDensity = 0f
    
    /**
     * 初始化适配器
     * 在Application的onCreate中调用
     */
    fun init(application: Application) {
        val displayMetrics = application.resources.displayMetrics
        
        if (systemDensity == 0f) {
            systemDensity = displayMetrics.density
            systemDensityDpi = displayMetrics.densityDpi
            systemScaledDensity = displayMetrics.scaledDensity
            
            // 监听字体缩放变化
            application.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onConfigurationChanged(newConfig: Configuration) {
                    if (newConfig.fontScale > 0) {
                        systemScaledDensity = application.resources.displayMetrics.scaledDensity
                    }
                }
                
                override fun onLowMemory() {}
            })
        }
        
        // 计算适配后的密度值
        calculateTargetDensity(application)
    }
    
    /**
     * 为Activity设置适配
     * 在Activity的onCreate中调用
     */
    fun setDensity(activity: Activity) {
        val application = activity.application
        
        if (targetDensity == 0f) {
            calculateTargetDensity(application)
        }
        
        // 设置Activity的密度
        val activityDisplayMetrics = activity.resources.displayMetrics
        activityDisplayMetrics.density = targetDensity
        activityDisplayMetrics.densityDpi = targetDensityDpi
        activityDisplayMetrics.scaledDensity = targetScaledDensity
        
        // 设置Application的密度（可选，某些情况下需要）
        val appDisplayMetrics = application.resources.displayMetrics
        appDisplayMetrics.density = targetDensity
        appDisplayMetrics.densityDpi = targetDensityDpi
        appDisplayMetrics.scaledDensity = targetScaledDensity
    }
    
    /**
     * 计算目标密度值
     */
    private fun calculateTargetDensity(application: Application) {
        val displayMetrics = application.resources.displayMetrics
        
        // 根据屏幕宽度计算缩放比例
        targetDensity = displayMetrics.widthPixels / DESIGN_WIDTH_IN_DP
        targetScaledDensity = targetDensity * (systemScaledDensity / systemDensity)
        targetDensityDpi = (160 * targetDensity).toInt()
    }
    
    /**
     * 恢复系统默认密度
     * 用于某些不需要适配的页面
     */
    fun restoreSystemDensity(activity: Activity) {
        val activityDisplayMetrics = activity.resources.displayMetrics
        activityDisplayMetrics.density = systemDensity
        activityDisplayMetrics.densityDpi = systemDensityDpi
        activityDisplayMetrics.scaledDensity = systemScaledDensity
        
        val appDisplayMetrics = activity.application.resources.displayMetrics
        appDisplayMetrics.density = systemDensity
        appDisplayMetrics.densityDpi = systemDensityDpi
        appDisplayMetrics.scaledDensity = systemScaledDensity
    }
    
    /**
     * 获取当前适配信息
     */
    fun getAdapterInfo(): AdapterInfo {
        return AdapterInfo(
            systemDensity = systemDensity,
            targetDensity = targetDensity,
            systemDensityDpi = systemDensityDpi,
            targetDensityDpi = targetDensityDpi,
            designWidthDp = DESIGN_WIDTH_IN_DP
        )
    }
}

/**
 * 适配信息数据类
 */
data class AdapterInfo(
    val systemDensity: Float,
    val targetDensity: Float,
    val systemDensityDpi: Int,
    val targetDensityDpi: Int,
    val designWidthDp: Float
)
