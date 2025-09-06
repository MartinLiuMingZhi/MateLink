# MateLink - 屏幕适配使用指南

## 📱 概述

本文档介绍MateLink项目中的屏幕适配解决方案，提供多种适配策略和工具，确保应用在不同尺寸设备上都能完美显示。

## 🎯 适配目标

- **全设备覆盖**：支持手机、平板、折叠屏等各种设备
- **响应式设计**：根据屏幕尺寸自动调整布局
- **像素完美**：确保设计稿的完美还原
- **性能优化**：适配方案轻量且高效

## 🛠️ 适配方案

### 1. 密度适配（推荐）

基于今日头条屏幕适配方案，通过修改系统密度实现适配。

#### 特点
- ✅ 适配精准，完美还原设计稿
- ✅ 侵入性小，无需修改现有布局
- ✅ 性能优秀，运行时开销极小
- ⚠️ 可能影响系统组件尺寸

#### 使用方法

```kotlin
// 1. 在Application中初始化
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DensityAdapterManager.init(this)
    }
}

// 2. 在Activity中设置适配
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // BaseActivity已自动处理适配
    }
}

// 3. 如需关闭适配（某些特殊页面）
class SpecialActivity : BaseActivity() {
    override fun isEnableScreenAdapter(): Boolean = false
}
```

### 2. Compose响应式布局

基于Material Design 3的响应式设计规范。

#### 屏幕断点
- **Compact**: < 600dp （手机）
- **Medium**: 600dp - 840dp （小平板）
- **Expanded**: > 840dp （大平板/桌面）

#### 使用示例

```kotlin
@Composable
fun MyScreen() {
    // 自适应布局
    AdaptiveLayout(
        compactContent = { PhoneLayout() },
        mediumContent = { TabletLayout() },
        expandedContent = { DesktopLayout() }
    )
}

@Composable
fun ResponsiveCard() {
    Card(
        modifier = Modifier
            .responsivePadding(
                compactPadding = 8.dp,
                mediumPadding = 16.dp,
                expandedPadding = 24.dp
            )
            .responsiveWidth(
                compactFraction = 1f,
                mediumFraction = 0.8f,
                expandedFraction = 0.6f
            )
    ) {
        // 内容
    }
}
```

### 3. 传统尺寸工具

提供传统的dp/px转换和屏幕信息获取。

```kotlin
// 获取屏幕信息
val screenWidth = ScreenUtils.getScreenWidth(context)
val isTablet = ScreenUtils.isTablet(context)
val statusBarHeight = ScreenUtils.getStatusBarHeight(context)

// 尺寸转换
val pxValue = ScreenUtils.dp2px(context, 16f)
val dpValue = ScreenUtils.px2dp(context, 48f)

// 适配转换
val adaptedWidth = ScreenUtils.getAdaptedWidth(context, 100f)
val adaptedHeight = ScreenUtils.getAdaptedHeight(context, 50f)
```

## 📐 使用指南

### 基础使用

#### 1. 扩展函数方式（推荐）

```kotlin
// 传统方式
val padding = ScreenUtils.dp2px(context, 16f)

// 扩展函数方式
val padding = 16f.dp2px(context)
val adaptedSize = 100f.adaptWidth(context)

// Compose中使用
@Composable
fun MyComponent() {
    Box(
        modifier = Modifier.size(50f.adaptedDp())
    ) {
        Text(
            text = "Hello",
            fontSize = 16f.adaptedSp()
        )
    }
}
```

#### 2. 响应式组件

```kotlin
@Composable
fun ResponsiveGrid() {
    ResponsiveColumns(
        compactColumns = 1,
        mediumColumns = 2,
        expandedColumns = 3
    ) { columns ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns)
        ) {
            // 网格内容
        }
    }
}

@Composable
fun ResponsiveSpacing() {
    Column {
        Text("Item 1")
        ResponsiveSpacer() // 自动调整间距
        Text("Item 2")
    }
}
```

#### 3. 窗口信息获取

```kotlin
@Composable
fun AdaptiveComponent() {
    val windowInfo = rememberWindowInfo()
    
    when (windowInfo.screenWidthInfo) {
        WindowSizeClass.COMPACT -> {
            // 紧凑布局逻辑
        }
        WindowSizeClass.MEDIUM -> {
            // 中等布局逻辑
        }
        WindowSizeClass.EXPANDED -> {
            // 展开布局逻辑
        }
    }
}
```

### 高级使用

#### 1. 自定义适配规则

```kotlin
// 自定义宽度适配
fun customAdaptWidth(context: Context, designWidth: Float): Int {
    val screenWidth = ScreenUtils.getScreenWidth(context)
    val screenWidthDp = ScreenUtils.px2dp(context, screenWidth.toFloat())
    
    // 自定义缩放逻辑
    val scale = when {
        screenWidthDp < 360 -> 0.9f  // 小屏幕缩小
        screenWidthDp > 500 -> 1.2f  // 大屏幕放大
        else -> 1.0f
    }
    
    return ScreenUtils.dp2px(context, designWidth * scale)
}
```

#### 2. 动态适配控制

```kotlin
class DynamicAdapterActivity : BaseActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 根据条件动态控制适配
        if (shouldUseAdapter()) {
            DensityAdapterManager.setDensity(this)
        }
    }
    
    private fun shouldUseAdapter(): Boolean {
        // 自定义判断逻辑
        return !ScreenUtils.isTablet(this)
    }
}
```

#### 3. 适配信息调试

```kotlin
// 获取适配信息
val adapterInfo = DensityAdapterManager.getAdapterInfo()
Log.d("ScreenAdapter", """
    系统密度: ${adapterInfo.systemDensity}
    适配密度: ${adapterInfo.targetDensity}
    设计宽度: ${adapterInfo.designWidthDp}dp
""")

// 屏幕信息调试
Log.d("ScreenInfo", """
    屏幕尺寸: ${context.screenWidthDp} x ${context.screenHeightDp} dp
    设备类型: ${if (context.isTablet) "平板" else "手机"}
    屏幕方向: ${if (context.isLandscape) "横屏" else "竖屏"}
""")
```

## 📏 设计规范

### 1. 尺寸标准

```kotlin
// 间距标准
object Spacing {
    val SMALL = 8.dp      // 小间距
    val MEDIUM = 16.dp    // 中等间距
    val LARGE = 24.dp     // 大间距
    val XLARGE = 32.dp    // 超大间距
}

// 字体大小标准
object FontSize {
    val SMALL = 12.sp     // 小字体
    val BODY = 14.sp      // 正文字体
    val TITLE = 16.sp     // 标题字体
    val HEADING = 20.sp   // 大标题字体
}

// 组件尺寸标准
object ComponentSize {
    val BUTTON_HEIGHT = 48.dp      // 按钮高度
    val INPUT_HEIGHT = 56.dp       // 输入框高度
    val TOOLBAR_HEIGHT = 56.dp     // 工具栏高度
    val FAB_SIZE = 56.dp           // 悬浮按钮尺寸
}
```

### 2. 响应式断点

```kotlin
@Composable
fun responsiveValue(
    compact: Dp,
    medium: Dp = compact,
    expanded: Dp = medium
): Dp {
    val windowInfo = rememberWindowInfo()
    return when (windowInfo.screenWidthInfo) {
        WindowSizeClass.COMPACT -> compact
        WindowSizeClass.MEDIUM -> medium
        WindowSizeClass.EXPANDED -> expanded
    }
}

// 使用示例
@Composable
fun MyCard() {
    Card(
        modifier = Modifier.padding(
            responsiveValue(
                compact = 8.dp,
                medium = 16.dp,
                expanded = 24.dp
            )
        )
    ) {
        // 内容
    }
}
```

## 🎨 最佳实践

### 1. 适配策略选择

| 场景 | 推荐方案 | 说明 |
|------|----------|------|
| **新项目** | 密度适配 + Compose响应式 | 完美适配 + 现代设计 |
| **老项目迁移** | 密度适配 | 侵入性小，快速适配 |
| **纯Compose项目** | Compose响应式 | 原生支持，性能最佳 |
| **设计稿还原** | 密度适配 | 像素级精确还原 |

### 2. 布局设计原则

```kotlin
@Composable
fun BestPracticeLayout() {
    // ✅ 推荐：响应式设计
    AdaptiveLayout(
        compactContent = {
            // 手机：垂直布局
            Column { /* 内容 */ }
        },
        mediumContent = {
            // 平板：混合布局
            Row { /* 内容 */ }
        },
        expandedContent = {
            // 桌面：多栏布局
            Row { /* 侧边栏 + 主内容 */ }
        }
    )
    
    // ❌ 避免：固定尺寸
    Box(modifier = Modifier.size(200.dp)) // 不推荐
    
    // ✅ 推荐：响应式尺寸
    Box(
        modifier = Modifier.size(
            responsiveValue(
                compact = 150.dp,
                medium = 200.dp,
                expanded = 250.dp
            )
        )
    )
}
```

### 3. 性能优化

```kotlin
// ✅ 推荐：缓存计算结果
class ScreenAdapterCache {
    private var cachedDensity: Float = 0f
    private var cachedScreenWidth: Int = 0
    
    fun getAdaptedSize(context: Context, designSize: Float): Int {
        if (cachedDensity == 0f) {
            cachedDensity = ScreenUtils.getScreenDensity(context)
            cachedScreenWidth = ScreenUtils.getScreenWidth(context)
        }
        
        // 使用缓存的值进行计算
        return (designSize * cachedDensity).toInt()
    }
}

// ✅ 推荐：使用remember缓存Compose计算
@Composable
fun OptimizedComponent() {
    val adaptedSize = remember {
        100f.adaptWidth(LocalContext.current)
    }
    
    Box(modifier = Modifier.size(adaptedSize.dp))
}
```

## 🐛 常见问题

### 1. 适配后系统组件异常

**问题**：使用密度适配后，系统对话框、Toast等组件尺寸异常

**解决方案**：
```kotlin
// 方案1：在特定页面恢复系统密度
override fun onResume() {
    super.onResume()
    if (needRestoreSystemDensity()) {
        DensityAdapterManager.restoreSystemDensity(this)
    }
}

// 方案2：仅对内容区域适配
class SelectiveAdapterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 不在Activity级别设置适配
        setContent {
            // 在Compose中使用适配工具
            MyAdaptedContent()
        }
    }
}
```

### 2. 字体缩放影响适配

**问题**：用户调整系统字体大小后，适配效果异常

**解决方案**：
```kotlin
// 监听字体缩放变化
class FontScaleAwareActivity : BaseActivity() {
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.fontScale != 1.0f) {
            // 重新设置适配
            DensityAdapterManager.setDensity(this)
        }
    }
}
```

### 3. 横竖屏切换适配

**问题**：横竖屏切换时适配效果不一致

**解决方案**：
```kotlin
@Composable
fun OrientationAwareLayout() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    if (isLandscape) {
        // 横屏布局
        Row { /* 内容 */ }
    } else {
        // 竖屏布局
        Column { /* 内容 */ }
    }
}
```

## 📊 测试与验证

### 1. 设备测试清单

| 设备类型 | 屏幕尺寸 | 测试要点 |
|----------|----------|----------|
| **小屏手机** | < 5.5英寸 | 内容是否完整显示 |
| **大屏手机** | > 6.5英寸 | 布局是否合理利用空间 |
| **小平板** | 7-9英寸 | 是否切换到平板布局 |
| **大平板** | > 10英寸 | 多栏布局是否正常 |
| **折叠屏** | 可变尺寸 | 展开/折叠适配是否正常 |

### 2. 适配验证工具

```kotlin
// 适配信息显示组件（调试用）
@Composable
fun DebugAdapterInfo() {
    if (BuildConfig.DEBUG) {
        val context = LocalContext.current
        val windowInfo = rememberWindowInfo()
        
        Column(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(8.dp)
        ) {
            Text("屏幕尺寸: ${windowInfo.screenWidth} x ${windowInfo.screenHeight}")
            Text("尺寸类型: ${windowInfo.screenWidthInfo}")
            Text("设备类型: ${if (context.isTablet) "平板" else "手机"}")
            Text("屏幕密度: ${ScreenUtils.getScreenDensity(context)}")
        }
    }
}
```

## 🎯 总结

MateLink的屏幕适配方案提供了：

1. **多种适配策略**：满足不同场景需求
2. **完整的工具链**：从基础工具到高级组件
3. **最佳实践指导**：确保正确使用
4. **性能优化**：保证流畅的用户体验

通过合理使用这些工具和组件，可以确保MateLink在各种设备上都能提供一致且优秀的用户体验。

---

**建议**：优先使用Compose响应式布局，在需要精确还原设计稿的场景下结合使用密度适配方案。
