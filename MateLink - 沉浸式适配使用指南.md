# MateLink 沉浸式适配使用指南

## 概述

沉浸式适配是 MateLink 项目中的重要功能，用于处理状态栏、导航栏的显示控制和内容区域的安全区域适配。本指南将详细介绍如何使用沉浸式适配功能。

## 核心组件

### 1. ImmersiveUtils 工具类

`ImmersiveUtils` 是沉浸式适配的核心工具类，提供以下功能：

#### 沉浸式模式类型
- `FULL_SCREEN`: 全屏模式（隐藏状态栏和导航栏）
- `STATUS_BAR_ONLY`: 仅隐藏状态栏
- `NAVIGATION_BAR_ONLY`: 仅隐藏导航栏
- `NORMAL`: 正常显示

#### 状态栏样式
- `LIGHT`: 浅色状态栏（深色文字）
- `DARK`: 深色状态栏（浅色文字）

#### 主要方法

```kotlin
// 设置沉浸式模式
ImmersiveUtils.setImmersiveMode(
    activity = activity,
    mode = ImmersiveUtils.ImmersiveMode.FULL_SCREEN,
    statusBarStyle = ImmersiveUtils.StatusBarStyle.DARK,
    statusBarColor = Color.TRANSPARENT
)

// 获取状态栏高度
val statusBarHeight = ImmersiveUtils.getStatusBarHeight(context)

// 获取导航栏高度
val navigationBarHeight = ImmersiveUtils.getNavigationBarHeight(context)

// 检查是否有导航栏
val hasNavBar = ImmersiveUtils.hasNavigationBar(context)

// 获取安全区域边距
val safeAreaInsets = ImmersiveUtils.getSafeAreaInsets(context)
```

### 2. ImmersiveComponents 组件

提供了一系列预制的 Compose 组件，自动处理安全区域适配：

#### ImmersiveContent
自动处理安全区域边距的内容容器：

```kotlin
ImmersiveContent {
    // 内容会自动适配安全区域
    Text("内容")
}
```

#### StatusBarSpacer 和 NavigationBarSpacer
为状态栏和导航栏预留空间的占位符：

```kotlin
Column {
    StatusBarSpacer() // 状态栏占位
    Text("内容")
    NavigationBarSpacer() // 导航栏占位
}
```

#### ImmersiveTopAppBar
自动适配状态栏高度的应用栏：

```kotlin
ImmersiveTopAppBar(
    title = { Text("标题") },
    navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Menu) } }
)
```

#### ImmersiveBottomNavigationBar
自动适配导航栏高度的底部导航栏：

```kotlin
ImmersiveBottomNavigationBar {
    NavigationBarItem(
        icon = { Icon(Icons.Default.Home) },
        label = { Text("首页") },
        selected = true,
        onClick = {}
    )
}
```

#### ImmersiveFloatingActionButton
自动适配底部安全区域的浮动操作按钮：

```kotlin
ImmersiveFloatingActionButton(
    onClick = { /* 点击事件 */ }
) {
    Icon(Icons.Default.Add)
}
```

#### safeAreaPadding 修饰符
为任何组件添加安全区域边距：

```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .safeAreaPadding() // 自动添加安全区域边距
) {
    // 内容
}
```

### 3. ImmersiveTheme 主题系统

#### ImmersiveThemeData
沉浸式主题数据类：

```kotlin
val theme = ImmersiveThemeData(
    statusBarColor = Color.Transparent,
    navigationBarColor = Color.Transparent,
    statusBarStyle = ImmersiveUtils.StatusBarStyle.DARK,
    immersiveMode = ImmersiveUtils.ImmersiveMode.NORMAL,
    isDarkTheme = false
)
```

#### 预设主题
`ImmersiveThemes` 对象提供了多种预设主题：

```kotlin
// 默认主题
val defaultTheme = ImmersiveThemes.default()

// 全屏主题
val fullScreenTheme = ImmersiveThemes.fullScreen()

// 仅状态栏主题
val statusBarTheme = ImmersiveThemes.statusBarOnly()

// 仅导航栏主题
val navigationBarTheme = ImmersiveThemes.navigationBarOnly()

// 自定义颜色主题
val customTheme = ImmersiveThemes.custom(
    statusBarColor = Color.Blue,
    navigationBarColor = Color.Red
)
```

#### ImmersiveTheme 组件
应用沉浸式主题的组件：

```kotlin
ImmersiveTheme(immersiveTheme) {
    // 内容
}
```

#### ImmersiveThemeState 状态管理
用于管理沉浸式主题状态：

```kotlin
val themeState = rememberImmersiveThemeState()

// 更新主题
themeState.updateImmersiveMode(ImmersiveUtils.ImmersiveMode.FULL_SCREEN)
themeState.updateStatusBarColor(Color.Blue)
themeState.toggleDarkTheme()
```

### 4. ImmersiveActivity 基类

`ImmersiveActivity` 是支持沉浸式显示的 Activity 基类：

```kotlin
class MyActivity : ImmersiveActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setImmersiveContent {
            MyScreen()
        }
    }
}
```

#### 主要方法

```kotlin
// 设置沉浸式主题
setImmersiveTheme(theme)

// 更新沉浸式主题
updateImmersiveTheme(theme)

// 切换全屏模式
toggleFullScreen()

// 设置状态栏颜色
setStatusBarColor(Color.Blue)

// 设置导航栏颜色
setNavigationBarColor(Color.Red)

// 设置状态栏样式
setStatusBarStyle(ImmersiveUtils.StatusBarStyle.LIGHT)

// 设置沉浸式模式
setImmersiveMode(ImmersiveUtils.ImmersiveMode.FULL_SCREEN)
```

## 使用示例

### 1. 基础使用

```kotlin
@Composable
fun MyScreen() {
    ImmersiveContent {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("标题")
            // 其他内容
        }
    }
}
```

### 2. 自定义沉浸式模式

```kotlin
@Composable
fun CustomImmersiveScreen() {
    var isFullScreen by remember { mutableStateOf(false) }
    
    val theme = rememberImmersiveTheme(
        immersiveMode = if (isFullScreen) 
            ImmersiveUtils.ImmersiveMode.FULL_SCREEN 
        else 
            ImmersiveUtils.ImmersiveMode.NORMAL
    )
    
    ImmersiveTheme(theme) {
        Column {
            Button(onClick = { isFullScreen = !isFullScreen }) {
                Text(if (isFullScreen) "退出全屏" else "进入全屏")
            }
            // 其他内容
        }
    }
}
```

### 3. 使用 ImmersiveActivity

```kotlin
class MainActivity : ImmersiveActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置初始沉浸式主题
        setImmersiveTheme(
            ImmersiveThemeData(
                immersiveMode = ImmersiveUtils.ImmersiveMode.STATUS_BAR_ONLY,
                statusBarStyle = ImmersiveUtils.StatusBarStyle.DARK
            )
        )
        
        setImmersiveContent {
            MainScreen()
        }
    }
}
```

### 4. 响应式沉浸式设计

```kotlin
@Composable
fun ResponsiveImmersiveScreen() {
    val themeState = rememberImmersiveThemeState()
    
    LaunchedEffect(Unit) {
        // 根据系统状态动态调整
        val isDarkTheme = isSystemInDarkTheme()
        themeState.updateTheme(
            ImmersiveThemeData(
                isDarkTheme = isDarkTheme,
                statusBarStyle = if (isDarkTheme) 
                    ImmersiveUtils.StatusBarStyle.LIGHT 
                else 
                    ImmersiveUtils.StatusBarStyle.DARK
            )
        )
    }
    
    ImmersiveTheme(themeState.theme) {
        // 内容
    }
}
```

## 最佳实践

### 1. 状态栏适配
- 使用 `StatusBarSpacer()` 为状态栏预留空间
- 根据内容背景色选择合适的状态栏样式
- 避免内容被状态栏遮挡

### 2. 导航栏适配
- 使用 `NavigationBarSpacer()` 为导航栏预留空间
- 确保重要操作按钮不被导航栏遮挡
- 考虑手势导航的影响

### 3. 安全区域适配
- 使用 `ImmersiveContent` 或 `safeAreaPadding()` 修饰符
- 测试不同设备的安全区域差异
- 考虑横竖屏切换的影响

### 4. 性能优化
- 使用 `remember` 缓存计算结果
- 避免频繁切换沉浸式模式
- 合理使用 `LaunchedEffect` 处理主题变化

### 5. 用户体验
- 提供明显的视觉反馈
- 保持一致的沉浸式体验
- 考虑用户的使用习惯

## 注意事项

1. **兼容性**: 不同 Android 版本的沉浸式 API 有差异，工具类已处理兼容性
2. **测试**: 在多种设备和系统版本上测试沉浸式效果
3. **性能**: 避免频繁切换沉浸式模式，可能影响性能
4. **用户体验**: 确保用户能够正常使用应用功能
5. **手势导航**: 考虑 Android 10+ 的手势导航对沉浸式的影响

## 演示页面

项目提供了完整的沉浸式适配演示页面 `ImmersiveDemoActivity`，展示了：
- 不同沉浸式模式的效果
- 状态栏样式的切换
- 主题模式的切换
- 各种组件的使用示例
- 安全区域适配效果

通过演示页面可以直观地了解沉浸式适配的各种功能和效果。
