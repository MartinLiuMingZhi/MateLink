package com.xichen.matelink.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xichen.matelink.core.ui.theme.*

/**
 * 主题演示和设置页面
 */
@Composable
fun ThemeDemo() {
    val scrollState = rememberScrollState()
    val themeState = rememberThemeState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 标题
        Text(
            text = "主题设置",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // 主题模式选择
        ThemeModeSelector(themeState)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 自定义主题选择
        CustomThemeSelector(themeState)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 动态颜色开关
        DynamicColorSwitch(themeState)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 主题预览
        ThemePreview()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 颜色展示
        ColorShowcase()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 字体展示
        TypographyShowcase()
    }
}

@Composable
fun ThemeModeSelector(themeState: ThemeState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "主题模式",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.selectableGroup()) {
                ThemeUtils.getAllThemeModes().forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (themeState.settings.themeMode == mode),
                                onClick = { themeState.onThemeModeChanged(mode) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (themeState.settings.themeMode == mode),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = ThemeUtils.getThemeModeDisplayName(mode),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomThemeSelector(themeState: ThemeState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "主题风格",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.selectableGroup()) {
                ThemeUtils.getAllCustomThemes().forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (themeState.settings.customTheme == theme),
                                onClick = { themeState.onCustomThemeChanged(theme) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (themeState.settings.customTheme == theme),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // 主题颜色预览
                        val previewColor = when (theme) {
                            CustomThemeType.DEFAULT -> MateLinkBlue
                            CustomThemeType.GREEN -> MateLinkGreen
                            CustomThemeType.PURPLE -> MateLinkPurple
                        }
                        
                        Card(
                            modifier = Modifier.size(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = previewColor
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {}
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = ThemeUtils.getCustomThemeDisplayName(theme),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DynamicColorSwitch(themeState: ThemeState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "动态颜色",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "跟随系统壁纸颜色 (Android 12+)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = themeState.settings.dynamicColor,
                onCheckedChange = themeState.onDynamicColorChanged
            )
        }
    }
}

@Composable
fun ThemePreview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "主题预览",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 模拟聊天界面
            Column {
                // 工具栏
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "好友群聊",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 消息气泡
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp, 4.dp, 12.dp, 12.dp),
                        modifier = Modifier.widthIn(max = 200.dp)
                    ) {
                        Text(
                            text = "这是我发送的消息",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(4.dp, 12.dp, 12.dp, 12.dp),
                        modifier = Modifier.widthIn(max = 200.dp)
                    ) {
                        Text(
                            text = "这是好友发送的消息",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 按钮组
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("主要按钮")
                    }
                    
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("次要按钮")
                    }
                }
            }
        }
    }
}

@Composable
fun ColorShowcase() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "颜色展示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 主色调
            ColorRow("主色调", MaterialTheme.colorScheme.primary)
            ColorRow("主色调容器", MaterialTheme.colorScheme.primaryContainer)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 次要色调
            ColorRow("次要色调", MaterialTheme.colorScheme.secondary)
            ColorRow("次要色调容器", MaterialTheme.colorScheme.secondaryContainer)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 第三色调
            ColorRow("第三色调", MaterialTheme.colorScheme.tertiary)
            ColorRow("第三色调容器", MaterialTheme.colorScheme.tertiaryContainer)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 表面色
            ColorRow("表面色", MaterialTheme.colorScheme.surface)
            ColorRow("表面变体", MaterialTheme.colorScheme.surfaceVariant)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 错误色
            ColorRow("错误色", MaterialTheme.colorScheme.error)
            ColorRow("错误容器", MaterialTheme.colorScheme.errorContainer)
        }
    }
}

@Composable
fun ColorRow(name: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(32.dp),
            colors = CardDefaults.cardColors(containerColor = color),
            shape = RoundedCornerShape(6.dp)
        ) {}
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun TypographyShowcase() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "字体展示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 各种字体样式展示
            TypographyRow("Display Large", MaterialTheme.typography.displayLarge)
            TypographyRow("Headline Large", MaterialTheme.typography.headlineLarge)
            TypographyRow("Title Large", MaterialTheme.typography.titleLarge)
            TypographyRow("Body Large", MaterialTheme.typography.bodyLarge)
            TypographyRow("Label Large", MaterialTheme.typography.labelLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 自定义字体样式
            Text(
                text = "自定义字体样式:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("聊天消息", style = CustomTextStyles.ChatMessage)
            Text("用户名", style = CustomTextStyles.UserName)
            Text("用户状态", style = CustomTextStyles.UserStatus)
            Text("按钮文字", style = CustomTextStyles.ButtonText)
            Text("错误提示", style = CustomTextStyles.ErrorText, color = MaterialTheme.colorScheme.error)
            Text("成功提示", style = CustomTextStyles.SuccessText, color = FunctionalColors.OnlineGreen)
        }
    }
}

@Composable
fun TypographyRow(name: String, textStyle: androidx.compose.ui.text.TextStyle) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "示例文字 Sample Text",
            style = textStyle
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeDemoPreview() {
    MateLinkTheme {
        ThemeDemo()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ThemeDemoDarkPreview() {
    MateLinkTheme {
        ThemeDemo()
    }
}

@Preview(showBackground = true)
@Composable
fun GreenThemePreview() {
    MateLinkGreenTheme {
        ThemeDemo()
    }
}

@Preview(showBackground = true)
@Composable
fun PurpleThemePreview() {
    MateLinkPurpleTheme {
        ThemeDemo()
    }
}
