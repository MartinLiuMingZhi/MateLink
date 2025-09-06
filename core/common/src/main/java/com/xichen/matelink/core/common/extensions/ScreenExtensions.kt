package com.xichen.matelink.core.common.extensions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xichen.matelink.core.common.utils.ScreenUtils

/**
 * 屏幕适配扩展函数
 * 提供便捷的适配方法
 */

/**
 * Int扩展：dp转px
 */
fun Int.dp2px(context: Context): Int = ScreenUtils.dp2px(context, this.toFloat())

/**
 * Float扩展：dp转px
 */
fun Float.dp2px(context: Context): Int = ScreenUtils.dp2px(context, this)

/**
 * Int扩展：px转dp
 */
fun Int.px2dp(context: Context): Int = ScreenUtils.px2dp(context, this.toFloat())

/**
 * Float扩展：px转dp
 */
fun Float.px2dp(context: Context): Int = ScreenUtils.px2dp(context, this)

/**
 * Int扩展：sp转px
 */
fun Int.sp2px(context: Context): Int = ScreenUtils.sp2px(context, this.toFloat())

/**
 * Float扩展：sp转px
 */
fun Float.sp2px(context: Context): Int = ScreenUtils.sp2px(context, this)

/**
 * Int扩展：px转sp
 */
fun Int.px2sp(context: Context): Int = ScreenUtils.px2sp(context, this.toFloat())

/**
 * Float扩展：px转sp
 */
fun Float.px2sp(context: Context): Int = ScreenUtils.px2sp(context, this)

/**
 * 宽度适配扩展
 */
fun Float.adaptWidth(context: Context): Int = ScreenUtils.getAdaptedWidth(context, this)

/**
 * 高度适配扩展
 */
fun Float.adaptHeight(context: Context): Int = ScreenUtils.getAdaptedHeight(context, this)

/**
 * 最小缩放适配扩展
 */
fun Float.adaptMinScale(context: Context): Int = ScreenUtils.getAdaptedSizeMinScale(context, this)

// ========== Compose扩展函数 ==========

/**
 * Compose中的dp适配
 */
@Composable
fun Float.adaptedDp(): Dp {
    val context = LocalContext.current
    val density = LocalDensity.current
    val adaptedPx = ScreenUtils.getAdaptedWidth(context, this)
    return with(density) { adaptedPx.toDp() }
}

/**
 * Compose中的sp适配
 */
@Composable
fun Float.adaptedSp(): TextUnit {
    val context = LocalContext.current
    val density = LocalDensity.current
    val adaptedPx = ScreenUtils.getAdaptedWidth(context, this)
    return with(density) { adaptedPx.toSp() }
}

/**
 * 响应式字体大小
 */
@Composable
fun responsiveTextSize(
    compactSize: Float = 14f,
    mediumSize: Float = 16f,
    expandedSize: Float = 18f
): TextUnit {
    val context = LocalContext.current
    val screenWidth = ScreenUtils.getScreenWidth(context)
    val screenWidthDp = ScreenUtils.px2dp(context, screenWidth.toFloat())
    
    val size = when {
        screenWidthDp < 600 -> compactSize
        screenWidthDp < 840 -> mediumSize
        else -> expandedSize
    }
    
    return size.sp
}

/**
 * 响应式间距
 */
@Composable
fun responsiveSpacing(
    compactSpacing: Float = 8f,
    mediumSpacing: Float = 16f,
    expandedSpacing: Float = 24f
): Dp {
    val context = LocalContext.current
    val screenWidth = ScreenUtils.getScreenWidth(context)
    val screenWidthDp = ScreenUtils.px2dp(context, screenWidth.toFloat())
    
    val spacing = when {
        screenWidthDp < 600 -> compactSpacing
        screenWidthDp < 840 -> mediumSpacing
        else -> expandedSpacing
    }
    
    return spacing.dp
}

// ========== 屏幕信息扩展 ==========

/**
 * Context扩展：获取屏幕宽度dp
 */
val Context.screenWidthDp: Int
    get() = ScreenUtils.px2dp(this, ScreenUtils.getScreenWidth(this).toFloat())

/**
 * Context扩展：获取屏幕高度dp
 */
val Context.screenHeightDp: Int
    get() = ScreenUtils.px2dp(this, ScreenUtils.getScreenHeight(this).toFloat())

/**
 * Context扩展：是否为平板
 */
val Context.isTablet: Boolean
    get() = ScreenUtils.isTablet(this)

/**
 * Context扩展：是否为横屏
 */
val Context.isLandscape: Boolean
    get() = ScreenUtils.isLandscape(this)

/**
 * Context扩展：状态栏高度dp
 */
val Context.statusBarHeightDp: Int
    get() = ScreenUtils.px2dp(this, ScreenUtils.getStatusBarHeight(this).toFloat())

/**
 * Context扩展：导航栏高度dp
 */
val Context.navigationBarHeightDp: Int
    get() = ScreenUtils.px2dp(this, ScreenUtils.getNavigationBarHeight(this).toFloat())
