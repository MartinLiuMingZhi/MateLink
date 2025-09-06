package com.xichen.matelink.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xichen.matelink.core.common.extensions.*
import com.xichen.matelink.core.common.utils.ScreenUtils
import com.xichen.matelink.core.ui.component.*
import com.xichen.matelink.ui.theme.MateLinkTheme

/**
 * 屏幕适配演示页面
 * 展示各种适配方案的使用效果
 */
@Composable
fun ScreenAdapterDemo() {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .responsivePadding()
    ) {
        // 标题
        Text(
            text = "屏幕适配演示",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 屏幕信息卡片
        ScreenInfoCard()
        
        ResponsiveSpacer()
        
        // 响应式布局演示
        ResponsiveLayoutDemo()
        
        ResponsiveSpacer()
        
        // 适配尺寸演示
        AdaptedSizeDemo()
        
        ResponsiveSpacer()
        
        // 响应式组件演示
        ResponsiveComponentDemo()
    }
}

@Composable
fun ScreenInfoCard() {
    val context = LocalContext.current
    val windowInfo = rememberWindowInfo()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "设备信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow("屏幕尺寸", "${context.screenWidthDp} x ${context.screenHeightDp} dp")
            InfoRow("窗口类型", windowInfo.screenWidthInfo.name)
            InfoRow("设备类型", if (context.isTablet) "平板" else "手机")
            InfoRow("屏幕方向", if (context.isLandscape) "横屏" else "竖屏")
            InfoRow("屏幕密度", "${ScreenUtils.getScreenDensity(context)}")
            InfoRow("状态栏高度", "${context.statusBarHeightDp} dp")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ResponsiveLayoutDemo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "响应式布局演示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AdaptiveLayout(
                compactContent = {
                    CompactLayoutExample()
                },
                mediumContent = {
                    MediumLayoutExample()
                },
                expandedContent = {
                    ExpandedLayoutExample()
                }
            )
        }
    }
}

@Composable
fun CompactLayoutExample() {
    Column {
        Text("紧凑布局 (手机)", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        
        repeat(3) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = "项目 ${index + 1}",
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun MediumLayoutExample() {
    Column {
        Text("中等布局 (小平板)", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(2) { index ->
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = "项目 ${index + 1}",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandedLayoutExample() {
    Column {
        Text("展开布局 (大平板)", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = "项目 ${index + 1}",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdaptedSizeDemo() {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "适配尺寸演示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 固定尺寸 vs 适配尺寸对比
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 固定尺寸
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("固定尺寸", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Color.Red.copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("100dp")
                    }
                }
                
                // 适配尺寸
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("适配尺寸", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(100f.adaptedDp())
                            .background(
                                Color.Green.copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("适配100dp")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 尺寸信息
            Text("适配信息:", fontWeight = FontWeight.Medium)
            Text("设计稿基准: 375dp")
            Text("当前屏幕: ${context.screenWidthDp}dp")
            Text("缩放比例: ${String.format("%.2f", context.screenWidthDp / 375f)}")
        }
    }
}

@Composable
fun ResponsiveComponentDemo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "响应式组件演示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 响应式网格
            Text("响应式网格:", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            
            ResponsiveColumns(
                compactColumns = 2,
                mediumColumns = 3,
                expandedColumns = 4
            ) { columns ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(columns) { index ->
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = "${index + 1}",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 响应式文字大小
            Text(
                text = "响应式字体大小",
                fontSize = responsiveTextSize(
                    compactSize = 14f,
                    mediumSize = 16f,
                    expandedSize = 18f
                )
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ScreenAdapterDemoPreview() {
    MateLinkTheme {
        ScreenAdapterDemo()
    }
}

@Preview(showBackground = true, widthDp = 700, heightDp = 1000)
@Composable
fun ScreenAdapterDemoTabletPreview() {
    MateLinkTheme {
        ScreenAdapterDemo()
    }
}
