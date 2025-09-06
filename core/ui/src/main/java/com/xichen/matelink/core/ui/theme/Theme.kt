package com.xichen.matelink.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * MateLink 应用主题配置
 * 支持深色/浅色模式、动态颜色、自定义主题
 */

// ========== 浅色主题配色方案 ==========
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    scrim = LightScrim,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary
)

// ========== 深色主题配色方案 ==========
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    scrim = DarkScrim,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary
)

// ========== 主题枚举 ==========
enum class ThemeMode {
    LIGHT,      // 浅色模式
    DARK,       // 深色模式
    SYSTEM      // 跟随系统
}

// ========== 主题配置 ==========
@Composable
fun MateLinkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // 动态颜色（Android 12+）
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // 动态颜色支持（Android 12+）
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // 深色主题
        darkTheme -> DarkColorScheme
        // 浅色主题
        else -> LightColorScheme
    }
    
    // 设置状态栏和导航栏颜色
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MateLinkTypography,
        shapes = MateLinkShapes,
        content = content
    )
}

// ========== 自定义主题变体 ==========

/**
 * 蓝色主题变体
 */
@Composable
fun MateLinkBlueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme.copy(
            primary = Color(0xFF90CAF9),
            primaryContainer = Color(0xFF1565C0),
            secondary = Color(0xFF81C784),
            tertiary = Color(0xFFFFB74D)
        )
    } else {
        LightColorScheme.copy(
            primary = MateLinkBlue,
            primaryContainer = Color(0xFFE3F2FD),
            secondary = MateLinkGreen,
            tertiary = MateLinkOrange
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MateLinkTypography,
        shapes = MateLinkShapes,
        content = content
    )
}

/**
 * 绿色主题变体
 */
@Composable
fun MateLinkGreenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme.copy(
            primary = Color(0xFFA5D6A7),
            primaryContainer = Color(0xFF2E7D32),
            secondary = Color(0xFF90CAF9),
            tertiary = Color(0xFFFFB74D)
        )
    } else {
        LightColorScheme.copy(
            primary = MateLinkGreen,
            primaryContainer = Color(0xFFE8F5E8),
            secondary = MateLinkBlue,
            tertiary = MateLinkOrange
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MateLinkTypography,
        shapes = MateLinkShapes,
        content = content
    )
}

/**
 * 紫色主题变体
 */
@Composable
fun MateLinkPurpleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme.copy(
            primary = Color(0xFFCE93D8),
            primaryContainer = Color(0xFF7B1FA2),
            secondary = Color(0xFF90CAF9),
            tertiary = Color(0xFFA5D6A7)
        )
    } else {
        LightColorScheme.copy(
            primary = MateLinkPurple,
            primaryContainer = Color(0xFFF3E5F5),
            secondary = MateLinkBlue,
            tertiary = MateLinkGreen
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MateLinkTypography,
        shapes = MateLinkShapes,
        content = content
    )
}

// ========== 主题工具函数 ==========

/**
 * 获取当前主题是否为深色
 */
@Composable
fun isDarkTheme(): Boolean {
    return isSystemInDarkTheme()
}

/**
 * 根据主题模式获取是否为深色主题
 */
@Composable
fun isDarkTheme(themeMode: ThemeMode): Boolean {
    return when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
}

/**
 * 主题切换函数
 */
@Composable
fun MateLinkThemeWithMode(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MateLinkTheme(
        darkTheme = isDarkTheme(themeMode),
        dynamicColor = dynamicColor,
        content = content
    )
}
