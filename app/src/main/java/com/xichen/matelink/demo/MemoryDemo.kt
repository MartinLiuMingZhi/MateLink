package com.xichen.matelink.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xichen.matelink.core.common.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 内存管理演示界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryDemo() {
    var memoryInfo by remember { mutableStateOf(MemoryLeakDetector.getMemoryInfo()) }
    var referenceCount by remember { mutableStateOf(MemoryLeakDetector.getReferenceCount()) }
    var optimizationSuggestions by remember { mutableStateOf(MemoryOptimizer.getOptimizationSuggestions()) }
    var isMonitoring by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    // 定期更新内存信息
    LaunchedEffect(isMonitoring) {
        while (isMonitoring) {
            memoryInfo = MemoryLeakDetector.getMemoryInfo()
            referenceCount = MemoryLeakDetector.getReferenceCount()
            optimizationSuggestions = MemoryOptimizer.getOptimizationSuggestions()
            delay(2000) // 每2秒更新一次
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题
        Text(
            text = "内存管理演示",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 控制按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { isMonitoring = !isMonitoring },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMonitoring) Color.Red else Color.Green
                )
            ) {
                Text(if (isMonitoring) "停止监控" else "开始监控")
            }
            
            Button(
                onClick = { 
                    scope.launch {
                        MemoryOptimizer.triggerOptimization(OptimizationLevel.MEDIUM)
                        memoryInfo = MemoryLeakDetector.getMemoryInfo()
                    }
                }
            ) {
                Text("触发优化")
            }
            
            Button(
                onClick = { 
                    scope.launch {
                        MemoryLeakDetector.forceGarbageCollection()
                        memoryInfo = MemoryLeakDetector.getMemoryInfo()
                    }
                }
            ) {
                Text("强制GC")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 内存使用情况卡片
            item {
                MemoryUsageCard(memoryInfo = memoryInfo)
            }
            
            // 引用计数卡片
            item {
                ReferenceCountCard(referenceCount = referenceCount)
            }
            
            // 内存优化建议卡片
            item {
                OptimizationSuggestionsCard(suggestions = optimizationSuggestions)
            }
            
            // 内存测试功能卡片
            item {
                MemoryTestCard()
            }
        }
    }
}

/**
 * 内存使用情况卡片
 */
@Composable
fun MemoryUsageCard(memoryInfo: MemoryInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "内存使用情况",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 内存使用率进度条
            val usagePercent = memoryInfo.memoryUsagePercent
            LinearProgressIndicator(
                progress = usagePercent / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    usagePercent > 80 -> Color.Red
                    usagePercent > 60 -> Color(0xFFFF9800)
                    else -> Color.Green
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "使用率: ${usagePercent.toInt()}%",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 详细内存信息
            MemoryInfoRow("最大内存", "${memoryInfo.maxMemoryMB}MB")
            MemoryInfoRow("已用内存", "${memoryInfo.usedMemoryMB}MB")
            MemoryInfoRow("可用内存", "${memoryInfo.availableMemoryMB}MB")
            MemoryInfoRow("堆内存", "${memoryInfo.heapMemoryMB}MB")
            
            if (memoryInfo.nativeHeapSize > 0) {
                MemoryInfoRow("Native堆大小", "${memoryInfo.nativeHeapSizeMB}MB")
                MemoryInfoRow("Native堆已用", "${memoryInfo.nativeHeapAllocatedMB}MB")
            }
        }
    }
}

/**
 * 引用计数卡片
 */
@Composable
fun ReferenceCountCard(referenceCount: ReferenceCount) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "引用计数",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            MemoryInfoRow("Activity引用", "${referenceCount.activityCount}")
            MemoryInfoRow("Fragment引用", "${referenceCount.fragmentCount}")
            MemoryInfoRow("总引用数", "${referenceCount.totalCount}")
        }
    }
}

/**
 * 优化建议卡片
 */
@Composable
fun OptimizationSuggestionsCard(suggestions: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "优化建议",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            suggestions.forEach { suggestion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• ",
                        color = Color.Blue,
                        fontSize = 16.sp
                    )
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * 内存测试功能卡片
 */
@Composable
fun MemoryTestCard() {
    var testResult by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "内存测试",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        testResult = performMemoryTest()
                    }
                ) {
                    Text("内存压力测试")
                }
                
                Button(
                    onClick = {
                        testResult = performLeakTest()
                    }
                ) {
                    Text("泄漏检测测试")
                }
            }
            
            if (testResult.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = testResult,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 内存信息行
 */
@Composable
fun MemoryInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 执行内存压力测试
 */
private fun performMemoryTest(): String {
    val beforeMemory = MemoryLeakDetector.getMemoryInfo()
    
    // 创建一些对象来测试内存
    val testObjects = mutableListOf<ByteArray>()
    repeat(100) {
        testObjects.add(ByteArray(1024 * 1024)) // 1MB each
    }
    
    val afterMemory = MemoryLeakDetector.getMemoryInfo()
    
    // 清理测试对象
    testObjects.clear()
    System.gc()
    
    val finalMemory = MemoryLeakDetector.getMemoryInfo()
    
    return """
        内存压力测试完成:
        测试前: ${beforeMemory.usedMemoryMB}MB
        测试后: ${afterMemory.usedMemoryMB}MB
        清理后: ${finalMemory.usedMemoryMB}MB
        内存增长: ${afterMemory.usedMemoryMB - beforeMemory.usedMemoryMB}MB
    """.trimIndent()
}

/**
 * 执行泄漏检测测试
 */
private fun performLeakTest(): String {
    val beforeCount = MemoryLeakDetector.getReferenceCount()
    
    // 模拟注册一些引用
    repeat(10) { index ->
        // 这里可以注册一些测试引用
        // MemoryLeakDetector.registerActivity(testActivity)
    }
    
    val afterCount = MemoryLeakDetector.getReferenceCount()
    
    return """
        泄漏检测测试完成:
        测试前引用数: ${beforeCount.totalCount}
        测试后引用数: ${afterCount.totalCount}
        新增引用: ${afterCount.totalCount - beforeCount.totalCount}
    """.trimIndent()
}
