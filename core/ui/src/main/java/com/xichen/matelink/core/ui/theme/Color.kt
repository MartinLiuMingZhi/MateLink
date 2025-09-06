package com.xichen.matelink.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * MateLink 应用颜色配置
 * 基于 Material Design 3 颜色系统
 */

// ========== 品牌主色调 ==========
val MateLinkBlue = Color(0xFF1976D2)        // 主蓝色 - 友联品牌色
val MateLinkBlueVariant = Color(0xFF1565C0) // 深蓝色变体
val MateLinkLightBlue = Color(0xFF42A5F5)   // 浅蓝色变体

// ========== 辅助色彩 ==========
val MateLinkGreen = Color(0xFF4CAF50)       // 成功/在线状态
val MateLinkOrange = Color(0xFFFF9800)      // 警告/通知
val MateLinkRed = Color(0xFFF44336)         // 错误/离线状态
val MateLinkPurple = Color(0xFF9C27B0)      // 特殊功能标识

// ========== 中性色彩 ==========
val MateLinkGray50 = Color(0xFFFAFAFA)
val MateLinkGray100 = Color(0xFFF5F5F5)
val MateLinkGray200 = Color(0xFFEEEEEE)
val MateLinkGray300 = Color(0xFFE0E0E0)
val MateLinkGray400 = Color(0xFFBDBDBD)
val MateLinkGray500 = Color(0xFF9E9E9E)
val MateLinkGray600 = Color(0xFF757575)
val MateLinkGray700 = Color(0xFF616161)
val MateLinkGray800 = Color(0xFF424242)
val MateLinkGray900 = Color(0xFF212121)

// ========== 浅色主题颜色 ==========
val LightPrimary = MateLinkBlue
val LightOnPrimary = Color.White
val LightPrimaryContainer = Color(0xFFE3F2FD)
val LightOnPrimaryContainer = Color(0xFF0D47A1)

val LightSecondary = Color(0xFF625B71)
val LightOnSecondary = Color.White
val LightSecondaryContainer = Color(0xFFE8DEF8)
val LightOnSecondaryContainer = Color(0xFF1D192B)

val LightTertiary = MateLinkGreen
val LightOnTertiary = Color.White
val LightTertiaryContainer = Color(0xFFE8F5E8)
val LightOnTertiaryContainer = Color(0xFF1B5E20)

val LightError = MateLinkRed
val LightOnError = Color.White
val LightErrorContainer = Color(0xFFFFEBEE)
val LightOnErrorContainer = Color(0xFFB71C1C)

val LightBackground = Color(0xFFFFFBFE)
val LightOnBackground = Color(0xFF1C1B1F)
val LightSurface = Color(0xFFFFFBFE)
val LightOnSurface = Color(0xFF1C1B1F)
val LightSurfaceVariant = Color(0xFFE7E0EC)
val LightOnSurfaceVariant = Color(0xFF49454F)

val LightOutline = Color(0xFF79747E)
val LightOutlineVariant = Color(0xFFCAC4D0)
val LightScrim = Color(0xFF000000)
val LightInverseSurface = Color(0xFF313033)
val LightInverseOnSurface = Color(0xFFF4EFF4)
val LightInversePrimary = Color(0xFF90CAF9)

// ========== 深色主题颜色 ==========
val DarkPrimary = Color(0xFF90CAF9)
val DarkOnPrimary = Color(0xFF003258)
val DarkPrimaryContainer = Color(0xFF004881)
val DarkOnPrimaryContainer = Color(0xFFBBDEFB)

val DarkSecondary = Color(0xFFCCC2DC)
val DarkOnSecondary = Color(0xFF332D41)
val DarkSecondaryContainer = Color(0xFF4A4458)
val DarkOnSecondaryContainer = Color(0xFFE8DEF8)

val DarkTertiary = Color(0xFFA5D6A7)
val DarkOnTertiary = Color(0xFF2E7D32)
val DarkTertiaryContainer = Color(0xFF388E3C)
val DarkOnTertiaryContainer = Color(0xFFC8E6C9)

val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

val DarkBackground = Color(0xFF10131C)
val DarkOnBackground = Color(0xFFE6E1E5)
val DarkSurface = Color(0xFF10131C)
val DarkOnSurface = Color(0xFFE6E1E5)
val DarkSurfaceVariant = Color(0xFF49454F)
val DarkOnSurfaceVariant = Color(0xFFCAC4D0)

val DarkOutline = Color(0xFF938F99)
val DarkOutlineVariant = Color(0xFF49454F)
val DarkScrim = Color(0xFF000000)
val DarkInverseSurface = Color(0xFFE6E1E5)
val DarkInverseOnSurface = Color(0xFF313033)
val DarkInversePrimary = Color(0xFF1976D2)

// ========== 功能性颜色 ==========
object FunctionalColors {
    // 在线状态
    val OnlineGreen = Color(0xFF4CAF50)
    val OnlineGreenDark = Color(0xFF2E7D32)
    
    // 离线状态
    val OfflineGray = Color(0xFF9E9E9E)
    val OfflineGrayDark = Color(0xFF616161)
    
    // 忙碌状态
    val BusyOrange = Color(0xFFFF9800)
    val BusyOrangeDark = Color(0xFFE65100)
    
    // 勿扰状态
    val DoNotDisturbRed = Color(0xFFF44336)
    val DoNotDisturbRedDark = Color(0xFFB71C1C)
    
    // 消息气泡
    val SentMessageBlue = Color(0xFF1976D2)
    val SentMessageBlueDark = Color(0xFF0D47A1)
    val ReceivedMessageGray = Color(0xFFEEEEEE)
    val ReceivedMessageGrayDark = Color(0xFF424242)
    
    // 链接颜色
    val LinkBlue = Color(0xFF1976D2)
    val LinkBlueDark = Color(0xFF90CAF9)
    
    // 警告颜色
    val WarningAmber = Color(0xFFFFC107)
    val WarningAmberDark = Color(0xFFFF8F00)
    
    // 信息颜色
    val InfoCyan = Color(0xFF00BCD4)
    val InfoCyanDark = Color(0xFF00ACC1)
}

// ========== 渐变色彩 ==========
object GradientColors {
    val PrimaryGradient = listOf(
        Color(0xFF1976D2),
        Color(0xFF42A5F5)
    )
    
    val SecondaryGradient = listOf(
        Color(0xFF9C27B0),
        Color(0xFFBA68C8)
    )
    
    val SuccessGradient = listOf(
        Color(0xFF4CAF50),
        Color(0xFF81C784)
    )
    
    val ErrorGradient = listOf(
        Color(0xFFF44336),
        Color(0xFFEF5350)
    )
}

// ========== 透明度变体 ==========
object AlphaColors {
    fun Color.alpha10() = this.copy(alpha = 0.1f)
    fun Color.alpha20() = this.copy(alpha = 0.2f)
    fun Color.alpha30() = this.copy(alpha = 0.3f)
    fun Color.alpha40() = this.copy(alpha = 0.4f)
    fun Color.alpha50() = this.copy(alpha = 0.5f)
    fun Color.alpha60() = this.copy(alpha = 0.6f)
    fun Color.alpha70() = this.copy(alpha = 0.7f)
    fun Color.alpha80() = this.copy(alpha = 0.8f)
    fun Color.alpha90() = this.copy(alpha = 0.9f)
}
