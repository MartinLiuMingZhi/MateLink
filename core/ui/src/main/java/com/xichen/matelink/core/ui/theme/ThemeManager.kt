package com.xichen.matelink.core.ui.theme

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 主题管理器
 * 负责主题的持久化存储和状态管理
 */

// DataStore 扩展
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")

// 主题设置键值
private object ThemeKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val CUSTOM_THEME = stringPreferencesKey("custom_theme")
}

// 自定义主题类型
enum class CustomThemeType {
    DEFAULT,    // 默认蓝色主题
    GREEN,      // 绿色主题
    PURPLE,     // 紫色主题
}

// 主题设置数据类
data class ThemeSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val customTheme: CustomThemeType = CustomThemeType.DEFAULT
)

/**
 * 主题管理器类
 */
class ThemeManager(private val context: Context) {
    
    // 主题设置流
    val themeSettings: Flow<ThemeSettings> = context.dataStore.data.map { preferences ->
        ThemeSettings(
            themeMode = ThemeMode.valueOf(
                preferences[ThemeKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            ),
            dynamicColor = preferences[ThemeKeys.DYNAMIC_COLOR] ?: false,
            customTheme = CustomThemeType.valueOf(
                preferences[ThemeKeys.CUSTOM_THEME] ?: CustomThemeType.DEFAULT.name
            )
        )
    }
    
    // 设置主题模式
    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[ThemeKeys.THEME_MODE] = themeMode.name
        }
    }
    
    // 设置动态颜色
    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ThemeKeys.DYNAMIC_COLOR] = enabled
        }
    }
    
    // 设置自定义主题
    suspend fun setCustomTheme(customTheme: CustomThemeType) {
        context.dataStore.edit { preferences ->
            preferences[ThemeKeys.CUSTOM_THEME] = customTheme.name
        }
    }
    
    // 重置为默认设置
    suspend fun resetToDefault() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

/**
 * Compose 主题状态管理
 */
@Composable
fun rememberThemeState(): ThemeState {
    val context = LocalContext.current
    val themeManager = remember { ThemeManager(context) }
    
    val themeSettings by themeManager.themeSettings.collectAsState(
        initial = ThemeSettings()
    )
    
    val coroutineScope = rememberCoroutineScope()
    
    return remember(themeSettings) {
        ThemeState(
            settings = themeSettings,
            onThemeModeChanged = { mode ->
                coroutineScope.launch {
                    themeManager.setThemeMode(mode)
                }
            },
            onDynamicColorChanged = { enabled ->
                coroutineScope.launch {
                    themeManager.setDynamicColor(enabled)
                }
            },
            onCustomThemeChanged = { theme ->
                coroutineScope.launch {
                    themeManager.setCustomTheme(theme)
                }
            },
            onResetToDefault = {
                coroutineScope.launch {
                    themeManager.resetToDefault()
                }
            }
        )
    }
}

/**
 * 主题状态类
 */
data class ThemeState(
    val settings: ThemeSettings,
    val onThemeModeChanged: (ThemeMode) -> Unit,
    val onDynamicColorChanged: (Boolean) -> Unit,
    val onCustomThemeChanged: (CustomThemeType) -> Unit,
    val onResetToDefault: () -> Unit
)

/**
 * 主题提供器 Composable
 */
@Composable
fun ThemeProvider(
    content: @Composable () -> Unit
) {
    val themeState = rememberThemeState()
    val settings = themeState.settings
    
    // 根据设置选择主题
    when (settings.customTheme) {
        CustomThemeType.DEFAULT -> {
            MateLinkThemeWithMode(
                themeMode = settings.themeMode,
                dynamicColor = settings.dynamicColor,
                content = content
            )
        }
        CustomThemeType.GREEN -> {
            MateLinkGreenTheme(
                darkTheme = isDarkTheme(settings.themeMode),
                content = content
            )
        }
        CustomThemeType.PURPLE -> {
            MateLinkPurpleTheme(
                darkTheme = isDarkTheme(settings.themeMode),
                content = content
            )
        }
    }
}

/**
 * 主题工具函数
 */
object ThemeUtils {
    
    /**
     * 获取主题模式的显示名称
     */
    fun getThemeModeDisplayName(themeMode: ThemeMode): String {
        return when (themeMode) {
            ThemeMode.LIGHT -> "浅色模式"
            ThemeMode.DARK -> "深色模式"
            ThemeMode.SYSTEM -> "跟随系统"
        }
    }
    
    /**
     * 获取自定义主题的显示名称
     */
    fun getCustomThemeDisplayName(customTheme: CustomThemeType): String {
        return when (customTheme) {
            CustomThemeType.DEFAULT -> "默认蓝色"
            CustomThemeType.GREEN -> "清新绿色"
            CustomThemeType.PURPLE -> "优雅紫色"
        }
    }
    
    /**
     * 获取所有可用的主题模式
     */
    fun getAllThemeModes(): List<ThemeMode> {
        return listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)
    }
    
    /**
     * 获取所有可用的自定义主题
     */
    fun getAllCustomThemes(): List<CustomThemeType> {
        return listOf(
            CustomThemeType.DEFAULT,
            CustomThemeType.GREEN,
            CustomThemeType.PURPLE
        )
    }
}
