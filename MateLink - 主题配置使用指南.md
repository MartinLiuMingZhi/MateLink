# MateLink - 主题配置使用指南

## 🎨 概述

MateLink的主题系统基于Material Design 3设计规范，提供了完整的深色/浅色模式支持、动态颜色、多种主题变体以及丰富的自定义选项。

## ✨ 主要特性

- 🌓 **深色/浅色模式**：完整支持，可跟随系统或手动切换
- 🎯 **动态颜色**：Android 12+支持，跟随系统壁纸
- 🎨 **多主题变体**：蓝色、绿色、紫色等多种风格
- 💾 **设置持久化**：用户设置自动保存
- 📱 **响应式设计**：适配不同屏幕尺寸
- 🛠️ **易于扩展**：便于添加新的主题变体

## 🚀 快速开始

### 1. 基础使用

```kotlin
@Composable
fun MyApp() {
    // 使用默认主题提供器
    ThemeProvider {
        // 您的应用内容
        MyContent()
    }
}

// 或者直接使用主题
@Composable
fun MyApp() {
    MateLinkTheme {
        MyContent()
    }
}
```

### 2. 主题模式控制

```kotlin
@Composable
fun MyApp() {
    // 强制浅色模式
    MateLinkTheme(darkTheme = false) {
        MyContent()
    }
    
    // 强制深色模式
    MateLinkTheme(darkTheme = true) {
        MyContent()
    }
    
    // 启用动态颜色
    MateLinkTheme(dynamicColor = true) {
        MyContent()
    }
}
```

### 3. 主题变体使用

```kotlin
@Composable
fun MyApp() {
    // 绿色主题
    MateLinkGreenTheme {
        MyContent()
    }
    
    // 紫色主题
    MateLinkPurpleTheme {
        MyContent()
    }
    
    // 蓝色主题（默认）
    MateLinkBlueTheme {
        MyContent()
    }
}
```

## 🎯 详细使用指南

### 颜色系统

#### 1. 标准Material 3颜色

```kotlin
@Composable
fun ColorExample() {
    Column {
        // 主色调
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = "主色调容器",
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        // 次要色调
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            onClick = { }
        ) {
            Text("次要按钮")
        }
    }
}
```

#### 2. 功能性颜色

```kotlin
@Composable
fun FunctionalColorExample() {
    Column {
        // 成功状态
        Text(
            text = "操作成功",
            color = ThemeColors.success
        )
        
        // 警告状态
        Text(
            text = "注意事项",
            color = ThemeColors.warning
        )
        
        // 错误状态
        Text(
            text = "操作失败",
            color = MaterialTheme.colorScheme.error
        )
        
        // 链接颜色
        Text(
            text = "点击链接",
            color = ThemeColors.link
        )
    }
}
```

#### 3. 消息气泡颜色

```kotlin
@Composable
fun MessageBubbleExample() {
    Column {
        // 发送的消息
        Box(
            modifier = Modifier.messageBubble(isSent = true)
        ) {
            Text(
                text = "我发送的消息",
                color = Color.White,
                modifier = Modifier.padding(12.dp)
            )
        }
        
        // 接收的消息
        Box(
            modifier = Modifier.messageBubble(isSent = false)
        ) {
            Text(
                text = "收到的消息",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
```

### 字体系统

#### 1. 标准Typography

```kotlin
@Composable
fun TypographyExample() {
    Column {
        Text(
            text = "大标题",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Text(
            text = "标题",
            style = MaterialTheme.typography.titleLarge
        )
        
        Text(
            text = "正文内容",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Text(
            text = "标签文字",
            style = MaterialTheme.typography.labelMedium
        )
    }
}
```

#### 2. 自定义字体样式

```kotlin
@Composable
fun CustomTypographyExample() {
    Column {
        // 聊天消息
        Text(
            text = "聊天消息内容",
            style = CustomTextStyles.ChatMessage
        )
        
        // 用户名
        Text(
            text = "用户昵称",
            style = CustomTextStyles.UserName
        )
        
        // 时间戳
        Text(
            text = "刚刚",
            style = CustomTextStyles.ChatTime
        )
        
        // 按钮文字
        Button(onClick = { }) {
            Text(
                text = "确认",
                style = CustomTextStyles.ButtonText
            )
        }
    }
}
```

### 形状系统

#### 1. 标准形状

```kotlin
@Composable
fun ShapeExample() {
    Column {
        // 小圆角卡片
        Card(
            shape = MaterialTheme.shapes.small
        ) {
            Text("小圆角", modifier = Modifier.padding(16.dp))
        }
        
        // 中圆角卡片
        Card(
            shape = MaterialTheme.shapes.medium
        ) {
            Text("中圆角", modifier = Modifier.padding(16.dp))
        }
        
        // 大圆角卡片
        Card(
            shape = MaterialTheme.shapes.large
        ) {
            Text("大圆角", modifier = Modifier.padding(16.dp))
        }
    }
}
```

#### 2. 自定义形状

```kotlin
@Composable
fun CustomShapeExample() {
    Column {
        // 消息气泡
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primary,
                    CustomShapes.MessageBubbleSent
                )
                .padding(12.dp)
        ) {
            Text("发送消息", color = Color.White)
        }
        
        // 圆形按钮
        Button(
            onClick = { },
            shape = CustomShapes.ButtonRound
        ) {
            Text("圆形按钮")
        }
        
        // 圆形头像
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    MaterialTheme.colorScheme.secondary,
                    CustomShapes.AvatarRound
                )
        )
    }
}
```

### 主题扩展

#### 1. 渐变背景

```kotlin
@Composable
fun GradientExample() {
    Column {
        // 主色调渐变
        Box(
            modifier = Modifier
                .size(100.dp)
                .primaryGradientBackground()
        ) {
            Text("主色调渐变", color = Color.White)
        }
        
        // 成功色渐变
        Box(
            modifier = Modifier
                .size(100.dp)
                .successGradientBackground()
        ) {
            Text("成功渐变", color = Color.White)
        }
        
        // 自定义渐变
        Box(
            modifier = Modifier
                .size(100.dp)
                .gradientBackground(
                    colors = listOf(Color.Blue, Color.Cyan)
                )
        ) {
            Text("自定义渐变", color = Color.White)
        }
    }
}
```

#### 2. 主题卡片样式

```kotlin
@Composable
fun ThemedCardExample() {
    Column {
        // 表面变体卡片
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .surfaceVariantBackground()
                .padding(16.dp)
        ) {
            Text("表面变体背景")
        }
        
        // 主容器色卡片
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .primaryContainerBackground()
                .padding(16.dp)
        ) {
            Text(
                text = "主容器背景",
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        // 自定义主题卡片
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .themeCard(
                    backgroundColor = MaterialTheme.colorScheme.tertiary,
                    borderColor = MaterialTheme.colorScheme.outline,
                    borderWidth = 1.dp
                )
                .padding(16.dp)
        ) {
            Text(
                text = "自定义主题卡片",
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}
```

### 主题管理

#### 1. 获取主题状态

```kotlin
@Composable
fun ThemeControlExample() {
    val themeState = rememberThemeState()
    
    Column {
        Text("当前主题: ${themeState.settings.themeMode}")
        Text("动态颜色: ${themeState.settings.dynamicColor}")
        Text("自定义主题: ${themeState.settings.customTheme}")
        
        // 切换主题按钮
        Button(
            onClick = {
                themeState.onThemeModeChanged(
                    if (themeState.settings.themeMode == ThemeMode.LIGHT) {
                        ThemeMode.DARK
                    } else {
                        ThemeMode.LIGHT
                    }
                )
            }
        ) {
            Text("切换主题")
        }
    }
}
```

#### 2. 主题设置页面

```kotlin
@Composable
fun ThemeSettingsScreen() {
    val themeState = rememberThemeState()
    
    LazyColumn {
        // 主题模式选择
        item {
            ThemeModeSelector(themeState)
        }
        
        // 主题风格选择
        item {
            CustomThemeSelector(themeState)
        }
        
        // 动态颜色开关
        item {
            DynamicColorSwitch(themeState)
        }
        
        // 重置按钮
        item {
            Button(
                onClick = { themeState.onResetToDefault() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("重置为默认")
            }
        }
    }
}
```

## 🎨 自定义主题

### 添加新的主题变体

```kotlin
// 1. 在CustomThemeType枚举中添加新类型
enum class CustomThemeType {
    DEFAULT, GREEN, PURPLE,
    ORANGE  // 新增橙色主题
}

// 2. 创建新的主题Composable
@Composable
fun MateLinkOrangeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme.copy(
            primary = Color(0xFFFFB74D),
            primaryContainer = Color(0xFFE65100)
        )
    } else {
        LightColorScheme.copy(
            primary = MateLinkOrange,
            primaryContainer = Color(0xFFFFF3E0)
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MateLinkTypography,
        shapes = MateLinkShapes,
        content = content
    )
}

// 3. 在ThemeProvider中添加处理逻辑
```

### 自定义颜色

```kotlin
// 扩展FunctionalColors
object FunctionalColors {
    // 添加新的功能色
    val CustomBlue = Color(0xFF2196F3)
    val CustomBlueDark = Color(0xFF0D47A1)
}

// 在ThemeColors中使用
object ThemeColors {
    val custom: Color
        @Composable get() = if (isDarkTheme()) 
            FunctionalColors.CustomBlueDark else FunctionalColors.CustomBlue
}
```

## 🛠️ 最佳实践

### 1. 颜色使用

```kotlin
// ✅ 推荐：使用主题颜色
Text(
    text = "内容",
    color = MaterialTheme.colorScheme.onSurface
)

// ❌ 避免：硬编码颜色
Text(
    text = "内容",
    color = Color.Black  // 深色模式下会有问题
)

// ✅ 推荐：使用功能性颜色
Text(
    text = "成功",
    color = ThemeColors.success
)
```

### 2. 形状使用

```kotlin
// ✅ 推荐：使用主题形状
Card(
    shape = MaterialTheme.shapes.medium
) {
    // 内容
}

// ✅ 推荐：使用自定义形状
Button(
    shape = CustomShapes.ButtonRound
) {
    Text("按钮")
}
```

### 3. 字体使用

```kotlin
// ✅ 推荐：使用主题字体
Text(
    text = "标题",
    style = MaterialTheme.typography.titleLarge
)

// ✅ 推荐：使用自定义字体样式
Text(
    text = "聊天消息",
    style = CustomTextStyles.ChatMessage
)
```

## 🔧 高级配置

### 1. 动态主题切换

```kotlin
@Composable
fun DynamicThemeExample() {
    var currentTheme by remember { mutableStateOf(CustomThemeType.DEFAULT) }
    
    when (currentTheme) {
        CustomThemeType.DEFAULT -> MateLinkTheme { Content() }
        CustomThemeType.GREEN -> MateLinkGreenTheme { Content() }
        CustomThemeType.PURPLE -> MateLinkPurpleTheme { Content() }
    }
    
    // 主题切换按钮
    Row {
        Button(onClick = { currentTheme = CustomThemeType.DEFAULT }) {
            Text("蓝色")
        }
        Button(onClick = { currentTheme = CustomThemeType.GREEN }) {
            Text("绿色")
        }
        Button(onClick = { currentTheme = CustomThemeType.PURPLE }) {
            Text("紫色")
        }
    }
}
```

### 2. 主题状态管理

```kotlin
@Composable
fun ThemeStateExample() {
    val themeState = rememberThemeState()
    
    // 监听主题变化
    LaunchedEffect(themeState.settings.themeMode) {
        // 主题模式改变时的处理逻辑
        when (themeState.settings.themeMode) {
            ThemeMode.DARK -> {
                // 深色模式特殊处理
            }
            ThemeMode.LIGHT -> {
                // 浅色模式特殊处理
            }
            ThemeMode.SYSTEM -> {
                // 跟随系统处理
            }
        }
    }
}
```

### 3. 条件主题应用

```kotlin
@Composable
fun ConditionalThemeExample() {
    val isSpecialMode = remember { mutableStateOf(false) }
    
    if (isSpecialMode.value) {
        // 特殊模式使用不同主题
        MateLinkPurpleTheme {
            SpecialContent()
        }
    } else {
        // 正常模式
        ThemeProvider {
            NormalContent()
        }
    }
}
```

## 📱 实际应用示例

### 聊天界面主题应用

```kotlin
@Composable
fun ChatScreen() {
    ThemeProvider {
        Column {
            // 工具栏
            TopAppBar(
                title = { Text("聊天") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            
            // 消息列表
            LazyColumn {
                items(messages) { message ->
                    MessageItem(message = message)
                }
            }
            
            // 输入框
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                shape = CustomShapes.TextField,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    val isSent = message.senderId == currentUserId
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSent) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 250.dp)
                .messageBubble(isSent = isSent)
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                style = CustomTextStyles.ChatMessage,
                color = if (isSent) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
```

## 📊 主题测试

### 预览配置

```kotlin
// 浅色模式预览
@Preview(showBackground = true, name = "Light Mode")
@Composable
fun LightPreview() {
    MateLinkTheme(darkTheme = false) {
        MyScreen()
    }
}

// 深色模式预览
@Preview(
    showBackground = true, 
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DarkPreview() {
    MateLinkTheme(darkTheme = true) {
        MyScreen()
    }
}

// 不同主题变体预览
@Preview(showBackground = true, name = "Green Theme")
@Composable
fun GreenThemePreview() {
    MateLinkGreenTheme {
        MyScreen()
    }
}
```

## 🎯 总结

MateLink的主题系统提供了：

1. **完整的Material 3支持**：标准颜色、字体、形状系统
2. **灵活的自定义选项**：多种主题变体和扩展机制
3. **用户友好的设置**：直观的主题切换和设置保存
4. **开发者友好**：丰富的工具函数和扩展
5. **性能优化**：高效的状态管理和缓存机制

通过这套主题系统，MateLink可以为用户提供个性化的视觉体验，同时保证开发效率和代码质量。
