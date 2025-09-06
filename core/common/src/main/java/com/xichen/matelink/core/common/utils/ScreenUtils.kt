package com.xichen.matelink.core.common.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.min

/**
 * 屏幕适配工具类
 * 提供多种屏幕适配方案和屏幕信息获取功能
 */
object ScreenUtils {
    
    // 设计稿基准尺寸（根据UI设计稿调整）
    private const val DESIGN_WIDTH_DP = 375f
    private const val DESIGN_HEIGHT_DP = 812f
    
    /**
     * 获取屏幕宽度（px）
     */
    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
    
    /**
     * 获取屏幕高度（px）
     */
    fun getScreenHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
    
    /**
     * 获取屏幕密度
     */
    fun getScreenDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }
    
    /**
     * 获取屏幕DPI
     */
    fun getScreenDensityDpi(context: Context): Int {
        return context.resources.displayMetrics.densityDpi
    }
    
    /**
     * dp转px
     */
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
    
    /**
     * px转dp
     */
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
    
    /**
     * sp转px
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
    
    /**
     * px转sp
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }
    
    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }
    
    /**
     * 获取导航栏高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }
    
    /**
     * 判断是否为平板
     */
    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout 
                and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
    
    /**
     * 判断是否为横屏
     */
    fun isLandscape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
    
    /**
     * 获取屏幕尺寸类型
     */
    fun getScreenSizeType(context: Context): ScreenSizeType {
        val screenLayout = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        return when (screenLayout) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> ScreenSizeType.SMALL
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> ScreenSizeType.NORMAL
            Configuration.SCREENLAYOUT_SIZE_LARGE -> ScreenSizeType.LARGE
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> ScreenSizeType.XLARGE
            else -> ScreenSizeType.NORMAL
        }
    }
    
    /**
     * 根据屏幕宽度适配
     * 基于设计稿宽度等比缩放
     */
    fun getAdaptedWidth(context: Context, designWidth: Float): Int {
        val screenWidth = getScreenWidth(context)
        val screenWidthDp = px2dp(context, screenWidth.toFloat())
        val scale = screenWidthDp / DESIGN_WIDTH_DP
        return dp2px(context, designWidth * scale)
    }
    
    /**
     * 根据屏幕高度适配
     * 基于设计稿高度等比缩放
     */
    fun getAdaptedHeight(context: Context, designHeight: Float): Int {
        val screenHeight = getScreenHeight(context)
        val screenHeightDp = px2dp(context, screenHeight.toFloat())
        val scale = screenHeightDp / DESIGN_HEIGHT_DP
        return dp2px(context, designHeight * scale)
    }
    
    /**
     * 最小宽高适配
     * 取宽高缩放比例的最小值，保证内容不被拉伸
     */
    fun getAdaptedSizeMinScale(context: Context, designSize: Float): Int {
        val screenWidth = getScreenWidth(context)
        val screenHeight = getScreenHeight(context)
        val screenWidthDp = px2dp(context, screenWidth.toFloat())
        val screenHeightDp = px2dp(context, screenHeight.toFloat())
        
        val scaleWidth = screenWidthDp / DESIGN_WIDTH_DP
        val scaleHeight = screenHeightDp / DESIGN_HEIGHT_DP
        val minScale = min(scaleWidth, scaleHeight)
        
        return dp2px(context, designSize * minScale)
    }
}

/**
 * 屏幕尺寸类型枚举
 */
enum class ScreenSizeType {
    SMALL,      // 小屏幕
    NORMAL,     // 普通屏幕
    LARGE,      // 大屏幕
    XLARGE      // 超大屏幕
}
