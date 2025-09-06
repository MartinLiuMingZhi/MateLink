package com.xichen.matelink.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * MateLink 应用形状配置
 * 基于 Material Design 3 形状系统
 */

// ========== Material Design 3 Shapes ==========
val MateLinkShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// ========== 自定义形状 ==========
object CustomShapes {
    // 消息气泡形状
    val MessageBubbleSent = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 4.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )
    
    val MessageBubbleReceived = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 16.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )
    
    // 卡片形状
    val CardSmall = RoundedCornerShape(8.dp)
    val CardMedium = RoundedCornerShape(12.dp)
    val CardLarge = RoundedCornerShape(16.dp)
    
    // 按钮形状
    val ButtonSmall = RoundedCornerShape(8.dp)
    val ButtonMedium = RoundedCornerShape(12.dp)
    val ButtonLarge = RoundedCornerShape(16.dp)
    val ButtonRound = RoundedCornerShape(50)
    
    // 输入框形状
    val TextField = RoundedCornerShape(8.dp)
    val TextFieldRound = RoundedCornerShape(50)
    
    // 对话框形状
    val Dialog = RoundedCornerShape(16.dp)
    val BottomSheet = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    
    // 头像形状
    val AvatarSmall = RoundedCornerShape(16.dp)
    val AvatarMedium = RoundedCornerShape(20.dp)
    val AvatarLarge = RoundedCornerShape(24.dp)
    val AvatarRound = RoundedCornerShape(50)
    
    // 徽章形状
    val Badge = RoundedCornerShape(50)
    val NotificationBadge = RoundedCornerShape(8.dp)
    
    // 图片形状
    val ImageSmall = RoundedCornerShape(8.dp)
    val ImageMedium = RoundedCornerShape(12.dp)
    val ImageLarge = RoundedCornerShape(16.dp)
    
    // 分隔线形状
    val Divider = RoundedCornerShape(1.dp)
    
    // 进度条形状
    val ProgressBar = RoundedCornerShape(4.dp)
    val ProgressBarRound = RoundedCornerShape(50)
}
