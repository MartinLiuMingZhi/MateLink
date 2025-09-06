package com.xichen.matelink.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * MateLink 应用字体配置
 * 基于 Material Design 3 字体系统
 */

// ========== 字体家族定义 ==========
// 默认使用系统字体，可根据需要添加自定义字体
val DefaultFontFamily = FontFamily.Default

// 如果需要自定义字体，可以添加字体资源
/*
val CustomFontFamily = FontFamily(
    Font(R.font.custom_regular, FontWeight.Normal),
    Font(R.font.custom_medium, FontWeight.Medium),
    Font(R.font.custom_bold, FontWeight.Bold),
    Font(R.font.custom_light, FontWeight.Light),
    Font(R.font.custom_thin, FontWeight.Thin)
)
*/

// ========== 字体权重定义 ==========
object FontWeights {
    val Thin = FontWeight.W100
    val ExtraLight = FontWeight.W200
    val Light = FontWeight.W300
    val Normal = FontWeight.W400
    val Medium = FontWeight.W500
    val SemiBold = FontWeight.W600
    val Bold = FontWeight.W700
    val ExtraBold = FontWeight.W800
    val Black = FontWeight.W900
}

// ========== Material Design 3 Typography ==========
val MateLinkTypography = Typography(
    // 显示文本 - 用于大标题
    displayLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    
    // 标题文本
    headlineLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // 标题文本
    titleLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // 正文文本
    bodyLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // 标签文本
    labelLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// ========== 自定义字体样式 ==========
object CustomTextStyles {
    // 聊天消息样式
    val ChatMessage = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )
    
    val ChatTime = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
    
    val ChatSenderName = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )
    
    // 用户状态样式
    val UserName = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )
    
    val UserStatus = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )
    
    // 导航样式
    val NavigationLabel = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    
    // 按钮样式
    val ButtonText = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    
    val ButtonTextLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    )
    
    // 输入框样式
    val InputText = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )
    
    val InputLabel = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
    
    val InputHint = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )
    
    // 错误提示样式
    val ErrorText = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
    
    // 成功提示样式
    val SuccessText = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
    
    // 数字徽章样式
    val BadgeText = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Bold,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp
    )
    
    // 工具栏标题
    val ToolbarTitle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Medium,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )
    
    // 副标题
    val Subtitle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    
    // 说明文字
    val Caption = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeights.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
    
    // 代码/等宽字体
    val CodeText = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeights.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )
}
