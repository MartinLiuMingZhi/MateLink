package com.xichen.matelink.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xichen.matelink.core.common.utils.MemoryUtils
import com.xichen.matelink.core.ui.component.*
import com.xichen.matelink.core.ui.image.ImageCacheManager
import com.xichen.matelink.core.ui.image.ImageCacheStatistics
import com.xichen.matelink.core.ui.theme.ThemeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 图片功能演示页面
 */
@Composable
fun ImageDemo(
    viewModel: ImageDemoViewModel = androidx.lifecycle.viewmodel.compose.hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val cacheStats by viewModel.cacheStats.collectAsState()
    val memoryStatus by viewModel.memoryStatus.collectAsState()
    var showImagePreview by remember { mutableStateOf(false) }
    var previewImages by remember { mutableStateOf<List<String>>(emptyList()) }
    var previewIndex by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 标题
        Text(
            text = "Coil图片框架演示",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // 内存状态卡片
        MemoryStatusCard(memoryStatus)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 缓存统计卡片
        CacheStatsCard(
            cacheStats = cacheStats,
            onClearCache = viewModel::clearCache,
            onRefreshStats = viewModel::refreshCacheStats
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 头像演示
        AvatarDemoCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 聊天图片演示
        ChatImageDemoCard { images, index ->
            previewImages = images
            previewIndex = index
            showImagePreview = true
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 朋友圈图片演示
        MomentImageDemoCard { images, index ->
            previewImages = images
            previewIndex = index
            showImagePreview = true
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 图片工具演示
        ImageUtilsCard(viewModel)
    }
    
    // 图片预览对话框
    if (showImagePreview) {
        ImagePreview(
            images = previewImages,
            initialIndex = previewIndex,
            onDismiss = { showImagePreview = false },
            onDownload = { url ->
                viewModel.downloadImage(url)
            },
            onShare = { url ->
                viewModel.shareImage(url)
            }
        )
    }
}

@Composable
fun MemoryStatusCard(memoryStatus: MemoryStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (memoryStatus) {
                MemoryStatus.GOOD -> MaterialTheme.colorScheme.primaryContainer
                MemoryStatus.MODERATE -> MaterialTheme.colorScheme.secondaryContainer
                MemoryStatus.HIGH -> MaterialTheme.colorScheme.tertiaryContainer
                MemoryStatus.CRITICAL -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "内存状态",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getMemoryStatusText(memoryStatus),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // 状态指示器
            Card(
                modifier = Modifier.size(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (memoryStatus) {
                        MemoryStatus.GOOD -> MaterialTheme.colorScheme.primary
                        MemoryStatus.MODERATE -> MaterialTheme.colorScheme.secondary
                        MemoryStatus.HIGH -> MaterialTheme.colorScheme.tertiary
                        MemoryStatus.CRITICAL -> MaterialTheme.colorScheme.error
                    }
                ),
                shape = RoundedCornerShape(6.dp)
            ) {}
        }
    }
}

@Composable
fun CacheStatsCard(
    cacheStats: ImageCacheStatistics?,
    onClearCache: () -> Unit,
    onRefreshStats: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "缓存统计",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row {
                    TextButton(onClick = onRefreshStats) {
                        Text("刷新")
                    }
                    TextButton(onClick = onClearCache) {
                        Text("清理")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (cacheStats != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CacheInfoRow("总缓存大小", formatBytes(cacheStats.getTotalSize()))
                    CacheInfoRow("内存缓存", formatBytes(cacheStats.getTotalMemorySize()))
                    CacheInfoRow("磁盘缓存", formatBytes(cacheStats.getTotalDiskSize()))
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "详细信息:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    
                    CacheInfoRow("默认缓存", formatBytes(cacheStats.defaultCache.memorySize + cacheStats.defaultCache.diskSize))
                    CacheInfoRow("头像缓存", formatBytes(cacheStats.avatarCache.memorySize + cacheStats.avatarCache.diskSize))
                    CacheInfoRow("大图缓存", formatBytes(cacheStats.largeImageCache.memorySize + cacheStats.largeImageCache.diskSize))
                    CacheInfoRow("缩略图缓存", formatBytes(cacheStats.thumbnailCache.memorySize + cacheStats.thumbnailCache.diskSize))
                }
            } else {
                Text("正在加载缓存统计...")
            }
        }
    }
}

@Composable
fun CacheInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AvatarDemoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "头像组件演示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    avatarUrl = null,
                    size = 40.dp,
                    showOnlineStatus = true,
                    isOnline = true
                )
                
                UserAvatar(
                    avatarUrl = null,
                    size = 48.dp,
                    showOnlineStatus = true,
                    isOnline = false
                )
                
                UserAvatar(
                    avatarUrl = null,
                    size = 56.dp
                )
                
                SpaceAvatar(
                    avatarUrl = null,
                    spaceName = "好友群",
                    size = 64.dp
                )
            }
        }
    }
}

@Composable
fun ChatImageDemoCard(
    onImageClick: (List<String>, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "聊天图片演示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val demoImages = listOf(
                "https://picsum.photos/400/300?random=1",
                "https://picsum.photos/400/300?random=2"
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChatImage(
                    imageUrl = demoImages[0],
                    onClick = { onImageClick(listOf(demoImages[0]), 0) }
                )
                
                MessageThumbnail(
                    imageUrl = demoImages[1],
                    onClick = { onImageClick(listOf(demoImages[1]), 0) }
                )
            }
        }
    }
}

@Composable
fun MomentImageDemoCard(
    onImageClick: (List<String>, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "朋友圈图片演示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val demoImages = listOf(
                "https://picsum.photos/400/400?random=1",
                "https://picsum.photos/400/400?random=2",
                "https://picsum.photos/400/400?random=3",
                "https://picsum.photos/400/400?random=4",
                "https://picsum.photos/400/400?random=5",
                "https://picsum.photos/400/400?random=6"
            )
            
            MomentImageGrid(
                images = demoImages,
                onImageClick = onImageClick
            )
        }
    }
}

@Composable
fun ImageUtilsCard(viewModel: ImageDemoViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "图片工具演示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.preloadImages() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("预加载图片")
                }
                
                Button(
                    onClick = { viewModel.compressImage() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("压缩图片")
                }
                
                Button(
                    onClick = { viewModel.cleanupExpiredCache() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("清理过期缓存")
                }
            }
        }
    }
}

// ========== ViewModel ==========

@HiltViewModel
class ImageDemoViewModel @Inject constructor(
    private val imageCacheManager: ImageCacheManager,
    private val memoryUtils: MemoryUtils
) : ViewModel() {
    
    private val _cacheStats = MutableStateFlow<ImageCacheStatistics?>(null)
    val cacheStats: StateFlow<ImageCacheStatistics?> = _cacheStats.asStateFlow()
    
    private val _memoryStatus = MutableStateFlow(MemoryStatus.GOOD)
    val memoryStatus: StateFlow<MemoryStatus> = _memoryStatus.asStateFlow()
    
    init {
        refreshCacheStats()
        updateMemoryStatus()
    }
    
    fun refreshCacheStats() {
        viewModelScope.launch {
            _cacheStats.value = imageCacheManager.getCacheStats()
        }
    }
    
    private fun updateMemoryStatus() {
        viewModelScope.launch {
            _memoryStatus.value = memoryUtils.getMemoryStatus()
        }
    }
    
    fun clearCache() {
        viewModelScope.launch {
            imageCacheManager.clearAllCache()
            refreshCacheStats()
            updateMemoryStatus()
        }
    }
    
    fun preloadImages() {
        viewModelScope.launch {
            val demoImages = listOf(
                "https://picsum.photos/800/600?random=10",
                "https://picsum.photos/800/600?random=11",
                "https://picsum.photos/800/600?random=12"
            )
            
            demoImages.forEach { url ->
                imageCacheManager.preloadImage(url)
            }
            
            refreshCacheStats()
        }
    }
    
    fun compressImage() {
        viewModelScope.launch {
            // 演示图片压缩
            val demoUrl = "https://picsum.photos/1200/900?random=20"
            // 实际项目中会调用ImageUtils.compressImage
            refreshCacheStats()
        }
    }
    
    fun cleanupExpiredCache() {
        viewModelScope.launch {
            imageCacheManager.cleanupExpiredCache(7) // 清理7天前的缓存
            refreshCacheStats()
        }
    }
    
    fun downloadImage(imageUrl: String) {
        viewModelScope.launch {
            // 实现图片下载逻辑
            // val result = imageUtils.saveImageToLocal(imageUrl)
        }
    }
    
    fun shareImage(imageUrl: String) {
        // 实现图片分享逻辑
    }
}

// ========== 工具函数 ==========

fun getMemoryStatusText(status: MemoryStatus): String {
    return when (status) {
        MemoryStatus.GOOD -> "良好 (< 50%)"
        MemoryStatus.MODERATE -> "中等 (50-75%)"
        MemoryStatus.HIGH -> "较高 (75-90%)"
        MemoryStatus.CRITICAL -> "危险 (> 90%)"
    }
}

fun formatBytes(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> String.format("%.2f GB", gb)
        mb >= 1 -> String.format("%.2f MB", mb)
        kb >= 1 -> String.format("%.2f KB", kb)
        else -> "$bytes B"
    }
}

@Preview(showBackground = true)
@Composable
fun ImageDemoPreview() {
    ThemeProvider {
        // 预览占位
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Coil图片框架演示",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserAvatar(avatarUrl = null, size = 48.dp)
                UserAvatar(avatarUrl = null, size = 56.dp)
                UserAvatar(avatarUrl = null, size = 64.dp)
            }
        }
    }
}
