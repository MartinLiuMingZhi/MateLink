package com.xichen.matelink.core.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 响应式布局组件
 * 根据屏幕尺寸自动调整布局
 */

/**
 * 屏幕断点定义
 */
object ScreenBreakpoints {
    val COMPACT_WIDTH = 600.dp      // 紧凑屏幕宽度
    val MEDIUM_WIDTH = 840.dp       // 中等屏幕宽度
    val EXPANDED_WIDTH = 1200.dp    // 展开屏幕宽度
    
    val COMPACT_HEIGHT = 480.dp     // 紧凑屏幕高度
    val MEDIUM_HEIGHT = 900.dp      // 中等屏幕高度
    val EXPANDED_HEIGHT = 1200.dp   // 展开屏幕高度
}

/**
 * 窗口尺寸类型
 */
enum class WindowSizeClass {
    COMPACT,    // 紧凑
    MEDIUM,     // 中等
    EXPANDED    // 展开
}

/**
 * 窗口信息
 */
data class WindowInfo(
    val screenWidthInfo: WindowSizeClass,
    val screenHeightInfo: WindowSizeClass,
    val screenWidth: Dp,
    val screenHeight: Dp
)

/**
 * 获取当前窗口信息
 */
@Composable
fun rememberWindowInfo(): WindowInfo {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    return remember(configuration) {
        val screenWidth = with(density) { configuration.screenWidthDp.dp }
        val screenHeight = with(density) { configuration.screenHeightDp.dp }
        
        WindowInfo(
            screenWidthInfo = when {
                screenWidth < ScreenBreakpoints.COMPACT_WIDTH -> WindowSizeClass.COMPACT
                screenWidth < ScreenBreakpoints.MEDIUM_WIDTH -> WindowSizeClass.MEDIUM
                else -> WindowSizeClass.EXPANDED
            },
            screenHeightInfo = when {
                screenHeight < ScreenBreakpoints.COMPACT_HEIGHT -> WindowSizeClass.COMPACT
                screenHeight < ScreenBreakpoints.MEDIUM_HEIGHT -> WindowSizeClass.MEDIUM
                else -> WindowSizeClass.EXPANDED
            },
            screenWidth = screenWidth,
            screenHeight = screenHeight
        )
    }
}

/**
 * 响应式列数组件
 * 根据屏幕宽度自动调整列数
 */
@Composable
fun ResponsiveColumns(
    modifier: Modifier = Modifier,
    compactColumns: Int = 1,
    mediumColumns: Int = 2,
    expandedColumns: Int = 3,
    content: @Composable (columns: Int) -> Unit
) {
    val windowInfo = rememberWindowInfo()
    
    val columns = when (windowInfo.screenWidthInfo) {
        WindowSizeClass.COMPACT -> compactColumns
        WindowSizeClass.MEDIUM -> mediumColumns
        WindowSizeClass.EXPANDED -> expandedColumns
    }
    
    Box(modifier = modifier) {
        content(columns)
    }
}

/**
 * 响应式间距组件
 * 根据屏幕尺寸调整间距
 */
@Composable
fun ResponsiveSpacer(
    compactSize: Dp = 8.dp,
    mediumSize: Dp = 16.dp,
    expandedSize: Dp = 24.dp
) {
    val windowInfo = rememberWindowInfo()
    
    val size = when (windowInfo.screenWidthInfo) {
        WindowSizeClass.COMPACT -> compactSize
        WindowSizeClass.MEDIUM -> mediumSize
        WindowSizeClass.EXPANDED -> expandedSize
    }
    
    Spacer(modifier = Modifier.size(size))
}

/**
 * 响应式内边距修饰符
 */
@Composable
fun Modifier.responsivePadding(
    compactPadding: Dp = 8.dp,
    mediumPadding: Dp = 16.dp,
    expandedPadding: Dp = 24.dp
): Modifier {
    val windowInfo = rememberWindowInfo()
    
    val padding = when (windowInfo.screenWidthInfo) {
        WindowSizeClass.COMPACT -> compactPadding
        WindowSizeClass.MEDIUM -> mediumPadding
        WindowSizeClass.EXPANDED -> expandedPadding
    }
    
    return this.padding(padding)
}

/**
 * 响应式宽度修饰符
 */
@Composable
fun Modifier.responsiveWidth(
    compactFraction: Float = 1f,
    mediumFraction: Float = 0.8f,
    expandedFraction: Float = 0.6f
): Modifier {
    val windowInfo = rememberWindowInfo()
    
    val fraction = when (windowInfo.screenWidthInfo) {
        WindowSizeClass.COMPACT -> compactFraction
        WindowSizeClass.MEDIUM -> mediumFraction
        WindowSizeClass.EXPANDED -> expandedFraction
    }
    
    return this.fillMaxWidth(fraction)
}

/**
 * 自适应布局组件
 * 根据屏幕尺寸选择不同的布局方式
 */
@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    compactContent: @Composable () -> Unit,
    mediumContent: @Composable () -> Unit = compactContent,
    expandedContent: @Composable () -> Unit = mediumContent
) {
    val windowInfo = rememberWindowInfo()
    
    Box(modifier = modifier) {
        when (windowInfo.screenWidthInfo) {
            WindowSizeClass.COMPACT -> compactContent()
            WindowSizeClass.MEDIUM -> mediumContent()
            WindowSizeClass.EXPANDED -> expandedContent()
        }
    }
}

/**
 * 响应式网格布局
 */
@Composable
fun ResponsiveGrid(
    modifier: Modifier = Modifier,
    items: List<@Composable () -> Unit>,
    compactColumns: Int = 1,
    mediumColumns: Int = 2,
    expandedColumns: Int = 3
) {
    ResponsiveColumns(
        modifier = modifier,
        compactColumns = compactColumns,
        mediumColumns = mediumColumns,
        expandedColumns = expandedColumns
    ) { columns ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items.size) { index ->
                items[index]()
            }
        }
    }
}
