package com.xichen.matelink.core.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 主题相关的扩展函数
 * 提供便捷的主题应用方法
 */

// ========== 颜色扩展 ==========

/**
 * 获取功能性颜色
 */
object ThemeColors {
    val success: Color
        @Composable get() = if (isDarkTheme()) FunctionalColors.OnlineGreenDark else FunctionalColors.OnlineGreen
    
    val warning: Color
        @Composable get() = if (isDarkTheme()) FunctionalColors.BusyOrangeDark else FunctionalColors.BusyOrange
    
    val info: Color
        @Composable get() = if (isDarkTheme()) FunctionalColors.InfoCyanDark else FunctionalColors.InfoCyan
    
    val link: Color
        @Composable get() = if (isDarkTheme()) FunctionalColors.LinkBlueDark else FunctionalColors.LinkBlue
    
    val sentMessage: Color
        @Composable get() = if (isDarkTheme()) FunctionalColors.SentMessageBlueDark else FunctionalColors.SentMessageBlue
    
    val receivedMessage: Color
        @Composable get() = if (isDarkTheme()) FunctionalColors.ReceivedMessageGrayDark else FunctionalColors.ReceivedMessageGray
}

// ========== 修饰符扩展 ==========

/**
 * 主题卡片样式
 */
@Composable
fun Modifier.themeCard(
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    borderWidth: Dp = 0.dp
): Modifier {
    return this
        .clip(shape)
        .background(backgroundColor)
        .then(
            if (borderWidth > 0.dp) {
                border(borderWidth, borderColor, shape)
            } else {
                Modifier
            }
        )
}

/**
 * 消息气泡样式
 */
@Composable
fun Modifier.messageBubble(
    isSent: Boolean,
    backgroundColor: Color? = null
): Modifier {
    val shape = if (isSent) CustomShapes.MessageBubbleSent else CustomShapes.MessageBubbleReceived
    val bgColor = backgroundColor ?: if (isSent) ThemeColors.sentMessage else ThemeColors.receivedMessage
    
    return this
        .clip(shape)
        .background(bgColor)
}

/**
 * 渐变背景
 */
@Composable
fun Modifier.gradientBackground(
    colors: List<Color>,
    shape: Shape = MaterialTheme.shapes.medium
): Modifier {
    return this
        .clip(shape)
        .background(
            brush = Brush.linearGradient(colors)
        )
}

/**
 * 主色调渐变背景
 */
@Composable
fun Modifier.primaryGradientBackground(
    shape: Shape = MaterialTheme.shapes.medium
): Modifier {
    val colors = if (isDarkTheme()) {
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
        )
    } else {
        GradientColors.PrimaryGradient
    }
    
    return gradientBackground(colors, shape)
}

/**
 * 成功色渐变背景
 */
@Composable
fun Modifier.successGradientBackground(
    shape: Shape = MaterialTheme.shapes.medium
): Modifier {
    return gradientBackground(GradientColors.SuccessGradient, shape)
}

/**
 * 错误色渐变背景
 */
@Composable
fun Modifier.errorGradientBackground(
    shape: Shape = MaterialTheme.shapes.medium
): Modifier {
    return gradientBackground(GradientColors.ErrorGradient, shape)
}

/**
 * 表面色变体背景
 */
@Composable
fun Modifier.surfaceVariantBackground(
    shape: Shape = MaterialTheme.shapes.medium
): Modifier {
    return this
        .clip(shape)
        .background(MaterialTheme.colorScheme.surfaceVariant)
}

/**
 * 主容器色背景
 */
@Composable
fun Modifier.primaryContainerBackground(
    shape: Shape = MaterialTheme.shapes.medium
): Modifier {
    return this
        .clip(shape)
        .background(MaterialTheme.colorScheme.primaryContainer)
}

// ========== 透明度工具函数 ==========

/**
 * 为颜色添加透明度
 */
@Composable
fun Color.withAlpha(alpha: Float): Color {
    return this.copy(alpha = alpha)
}

/**
 * 获取禁用状态的颜色
 */
@Composable
fun Color.disabled(): Color {
    return this.copy(alpha = 0.38f)
}

/**
 * 获取悬停状态的颜色
 */
@Composable
fun Color.hovered(): Color {
    return this.copy(alpha = 0.08f)
}

/**
 * 获取按下状态的颜色
 */
@Composable
fun Color.pressed(): Color {
    return this.copy(alpha = 0.12f)
}

/**
 * 获取选中状态的颜色
 */
@Composable
fun Color.selected(): Color {
    return this.copy(alpha = 0.16f)
}

// ========== 状态颜色获取 ==========

/**
 * 在线状态颜色
 */
@Composable
fun onlineStatusColor(): Color {
    return if (isDarkTheme()) FunctionalColors.OnlineGreenDark else FunctionalColors.OnlineGreen
}

/**
 * 离线状态颜色
 */
@Composable
fun offlineStatusColor(): Color {
    return if (isDarkTheme()) FunctionalColors.OfflineGrayDark else FunctionalColors.OfflineGray
}

/**
 * 忙碌状态颜色
 */
@Composable
fun busyStatusColor(): Color {
    return if (isDarkTheme()) FunctionalColors.BusyOrangeDark else FunctionalColors.BusyOrange
}

/**
 * 勿扰状态颜色
 */
@Composable
fun doNotDisturbStatusColor(): Color {
    return if (isDarkTheme()) FunctionalColors.DoNotDisturbRedDark else FunctionalColors.DoNotDisturbRed
}
