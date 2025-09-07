package com.xichen.matelink.core.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * 沉浸式适配工具类
 * 提供状态栏、导航栏的沉浸式显示控制
 */
object ImmersiveUtils {

    /**
     * 沉浸式模式类型
     */
    enum class ImmersiveMode {
        FULL_SCREEN,        // 全屏模式（隐藏状态栏和导航栏）
        STATUS_BAR_ONLY,    // 仅隐藏状态栏
        NAVIGATION_BAR_ONLY, // 仅隐藏导航栏
        NORMAL              // 正常显示
    }

    /**
     * 状态栏样式
     */
    enum class StatusBarStyle {
        LIGHT,  // 浅色状态栏（深色文字）
        DARK    // 深色状态栏（浅色文字）
    }

    /**
     * 设置沉浸式模式
     */
    fun setImmersiveMode(
        activity: Activity,
        mode: ImmersiveMode,
        statusBarStyle: StatusBarStyle = StatusBarStyle.DARK,
        statusBarColor: Int = Color.TRANSPARENT
    ) {
        val window = activity.window
        val decorView = window.decorView

        when (mode) {
            ImmersiveMode.FULL_SCREEN -> {
                // 全屏模式
                setFullScreen(activity, statusBarStyle, statusBarColor)
            }
            ImmersiveMode.STATUS_BAR_ONLY -> {
                // 仅隐藏状态栏
                hideStatusBar(activity, statusBarStyle, statusBarColor)
            }
            ImmersiveMode.NAVIGATION_BAR_ONLY -> {
                // 仅隐藏导航栏
                hideNavigationBar(activity, statusBarStyle, statusBarColor)
            }
            ImmersiveMode.NORMAL -> {
                // 正常显示
                showSystemBars(activity, statusBarStyle, statusBarColor)
            }
        }
    }

    /**
     * 设置全屏模式
     */
    private fun setFullScreen(
        activity: Activity,
        statusBarStyle: StatusBarStyle,
        statusBarColor: Int
    ) {
        val window = activity.window
        val decorView = window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ 使用新的 WindowInsetsController
            val controller = WindowInsetsControllerCompat(window, decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            // Android 11 以下使用传统方法
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
        }

        // 设置状态栏颜色和样式
        setStatusBarColor(window, statusBarColor)
        setStatusBarStyle(window, statusBarStyle)
    }

    /**
     * 隐藏状态栏
     */
    private fun hideStatusBar(
        activity: Activity,
        statusBarStyle: StatusBarStyle,
        statusBarColor: Int
    ) {
        val window = activity.window
        val decorView = window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = WindowInsetsControllerCompat(window, decorView)
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
        }

        setStatusBarColor(window, statusBarColor)
        setStatusBarStyle(window, statusBarStyle)
    }

    /**
     * 隐藏导航栏
     */
    private fun hideNavigationBar(
        activity: Activity,
        statusBarStyle: StatusBarStyle,
        statusBarColor: Int
    ) {
        val window = activity.window
        val decorView = window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = WindowInsetsControllerCompat(window, decorView)
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )
        }

        setStatusBarColor(window, statusBarColor)
        setStatusBarStyle(window, statusBarStyle)
    }

    /**
     * 显示系统栏
     */
    private fun showSystemBars(
        activity: Activity,
        statusBarStyle: StatusBarStyle,
        statusBarColor: Int
    ) {
        val window = activity.window
        val decorView = window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = WindowInsetsControllerCompat(window, decorView)
            controller.show(WindowInsetsCompat.Type.systemBars())
        } else {
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }

        setStatusBarColor(window, statusBarColor)
        setStatusBarStyle(window, statusBarStyle)
    }

    /**
     * 设置状态栏颜色
     */
    private fun setStatusBarColor(window: android.view.Window, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }

    /**
     * 设置状态栏样式
     */
    private fun setStatusBarStyle(window: android.view.Window, style: StatusBarStyle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            var flags = decorView.systemUiVisibility

            when (style) {
                StatusBarStyle.LIGHT -> {
                    flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                StatusBarStyle.DARK -> {
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }

            decorView.systemUiVisibility = flags
        }
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 获取导航栏高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 检查是否有导航栏
     */
    fun hasNavigationBar(context: Context): Boolean {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsets(WindowInsets.Type.navigationBars())
            insets.bottom > 0
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            val realSize = android.graphics.Point()
            val screenSize = android.graphics.Point()
            display.getRealSize(realSize)
            display.getSize(screenSize)
            realSize.y > screenSize.y
        }
    }

    /**
     * 获取安全区域边距
     */
    fun getSafeAreaInsets(context: Context): SafeAreaInsets {
        return SafeAreaInsets(
            top = getStatusBarHeight(context),
            bottom = if (hasNavigationBar(context)) getNavigationBarHeight(context) else 0,
            left = 0,
            right = 0
        )
    }

    /**
     * 设置窗口标志
     */
    fun setWindowFlags(activity: Activity, flags: Int, mask: Int) {
        val window = activity.window
        window.setFlags(flags, mask)
    }

    /**
     * 清除窗口标志
     */
    fun clearWindowFlags(activity: Activity, flags: Int) {
        val window = activity.window
        window.clearFlags(flags)
    }

    /**
     * 设置保持屏幕常亮
     */
    fun keepScreenOn(activity: Activity) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * 取消保持屏幕常亮
     */
    fun clearKeepScreenOn(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

/**
 * 安全区域边距数据类
 */
data class SafeAreaInsets(
    val top: Int,
    val bottom: Int,
    val left: Int,
    val right: Int
) {
    val totalVertical: Int get() = top + bottom
    val totalHorizontal: Int get() = left + right
}
