package com.xichen.matelink.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xichen.matelink.core.ui.component.*
import com.xichen.matelink.core.ui.image.ImageLoaderManager
import javax.inject.Inject

/**
 * 图片组件演示
 * 展示各种图片组件的使用方法
 */

@Composable
fun ImageDemo(
    imageLoaderManager: ImageLoaderManager? = null
) {
    var showPreview by remember { mutableStateOf(false) }
    var previewImageUrl by remember { mutableStateOf("") }
    
    val sampleImages = listOf(
        "https://picsum.photos/300/200?random=1",
        "https://picsum.photos/300/200?random=2",
        "https://picsum.photos/300/200?random=3",
        "https://picsum.photos/300/200?random=4",
        "https://picsum.photos/300/200?random=5"
    )
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "图片组件演示",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        
        item {
            Text(
                text = "基础图片组件",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            BaseImage(
                data = sampleImages[0],
                contentDescription = "基础图片",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                imageLoaderManager = imageLoaderManager
            )
        }
        
        item {
            Text(
                text = "圆形图片组件",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleImage(
                    data = sampleImages[1],
                    contentDescription = "圆形图片",
                    size = 60.dp,
                    borderWidth = 2.dp,
                    imageLoaderManager = imageLoaderManager
                )
                
                CircleImage(
                    data = sampleImages[2],
                    contentDescription = "圆形图片",
                    size = 80.dp,
                    borderWidth = 3.dp,
                    imageLoaderManager = imageLoaderManager
                )
            }
        }
        
        item {
            Text(
                text = "圆角图片组件",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            RoundedImage(
                data = sampleImages[3],
                contentDescription = "圆角图片",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                cornerRadius = 16.dp,
                imageLoaderManager = imageLoaderManager
            )
        }
        
        item {
            Text(
                text = "带加载状态的图片",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            LoadingImage(
                data = sampleImages[4],
                contentDescription = "加载图片",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                imageLoaderManager = imageLoaderManager
            )
        }
        
        item {
            Text(
                text = "可点击的图片",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            ClickableImage(
                data = sampleImages[0],
                contentDescription = "可点击图片",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                onClick = {
                    previewImageUrl = sampleImages[0]
                    showPreview = true
                },
                imageLoaderManager = imageLoaderManager
            )
        }
        
        item {
            Text(
                text = "头像组件",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarImage(
                    data = sampleImages[1],
                    contentDescription = "头像",
                    size = 48.dp,
                    imageLoaderManager = imageLoaderManager
                )
                
                AvatarImage(
                    data = sampleImages[2],
                    contentDescription = "头像",
                    size = 64.dp,
                    imageLoaderManager = imageLoaderManager
                )
                
                AvatarImage(
                    data = sampleImages[3],
                    contentDescription = "头像",
                    size = 80.dp,
                    imageLoaderManager = imageLoaderManager
                )
            }
        }
        
        item {
            Text(
                text = "缩略图组件",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThumbnailImage(
                    data = sampleImages[0],
                    contentDescription = "缩略图",
                    size = 80.dp,
                    imageLoaderManager = imageLoaderManager
                )
                
                ThumbnailImage(
                    data = sampleImages[1],
                    contentDescription = "缩略图",
                    size = 80.dp,
                    imageLoaderManager = imageLoaderManager
                )
                
                ThumbnailImage(
                    data = sampleImages[2],
                    contentDescription = "缩略图",
                    size = 80.dp,
                    imageLoaderManager = imageLoaderManager
                )
            }
        }
        
        item {
            Text(
                text = "横幅图片组件",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            BannerImage(
                data = sampleImages[4],
                contentDescription = "横幅图片",
                height = 200.dp,
                cornerRadius = 12.dp,
                imageLoaderManager = imageLoaderManager
            )
        }
        
        item {
            Text(
                text = "网格图片组件",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            ImageGridPreview(
                imageUrls = sampleImages,
                columns = 3,
                onImageClick = { index ->
                    previewImageUrl = sampleImages[index]
                    showPreview = true
                },
                imageLoaderManager = imageLoaderManager
            )
        }
    }
    
    // 图片预览对话框
    if (showPreview) {
        ImagePreviewDialog(
            imageUrl = previewImageUrl,
            onDismiss = { showPreview = false },
            imageLoaderManager = imageLoaderManager
        )
    }
}
