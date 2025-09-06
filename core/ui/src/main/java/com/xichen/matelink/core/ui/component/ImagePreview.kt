package com.xichen.matelink.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.xichen.matelink.core.ui.theme.ThemeProvider
import kotlin.math.abs

/**
 * 图片预览组件
 * 支持缩放、滑动、多图浏览
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePreview(
    images: List<String>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit,
    onDownload: ((String) -> Unit)? = null,
    onShare: ((String) -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (images.size == 1) {
                // 单图预览
                SingleImagePreview(
                    imageUrl = images[0],
                    onDismiss = onDismiss,
                    onDownload = onDownload,
                    onShare = onShare
                )
            } else {
                // 多图预览
                MultiImagePreview(
                    images = images,
                    initialIndex = initialIndex,
                    onDismiss = onDismiss,
                    onDownload = onDownload,
                    onShare = onShare
                )
            }
        }
    }
}

@Composable
private fun SingleImagePreview(
    imageUrl: String,
    onDismiss: () -> Unit,
    onDownload: ((String) -> Unit)?,
    onShare: ((String) -> Unit)?
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 图片显示
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .build(),
            contentDescription = "预览图片",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 3f)
                        
                        val maxX = (size.width * (scale - 1)) / 2
                        val maxY = (size.height * (scale - 1)) / 2
                        
                        offset = Offset(
                            x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                            y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            scale = if (scale > 1f) 1f else 2f
                            offset = Offset.Zero
                        }
                    )
                },
            contentScale = ContentScale.Fit
        )
        
        // 顶部操作栏
        ImagePreviewTopBar(
            onClose = onDismiss,
            modifier = Modifier.align(Alignment.TopStart)
        )
        
        // 底部操作栏
        ImagePreviewBottomBar(
            imageUrl = imageUrl,
            onDownload = onDownload,
            onShare = onShare,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MultiImagePreview(
    images: List<String>,
    initialIndex: Int,
    onDismiss: () -> Unit,
    onDownload: ((String) -> Unit)?,
    onShare: ((String) -> Unit)?
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { images.size }
    )
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 图片轮播
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ZoomableImage(
                imageUrl = images[page],
                onDismiss = onDismiss
            )
        }
        
        // 顶部操作栏
        ImagePreviewTopBar(
            onClose = onDismiss,
            title = "${pagerState.currentPage + 1} / ${images.size}",
            modifier = Modifier.align(Alignment.TopStart)
        )
        
        // 底部操作栏
        ImagePreviewBottomBar(
            imageUrl = images[pagerState.currentPage],
            onDownload = onDownload,
            onShare = onShare,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        // 页面指示器
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (index == pagerState.currentPage) {
                                    Color.White
                                } else {
                                    Color.White.copy(alpha = 0.5f)
                                },
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ZoomableImage(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .build(),
        contentDescription = "可缩放图片",
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 3f)
                    
                    val maxX = (size.width * (scale - 1)) / 2
                    val maxY = (size.height * (scale - 1)) / 2
                    
                    offset = Offset(
                        x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                        y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                    )
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale > 1f) 1f else 2f
                        offset = Offset.Zero
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    if (scale <= 1f && abs(change.y) > abs(change.x)) {
                        // 向下滑动关闭
                        if (change.y > 100) {
                            onDismiss()
                        }
                    }
                }
            },
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun ImagePreviewTopBar(
    onClose: () -> Unit,
    title: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color.Black.copy(alpha = 0.5f),
                RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title != null) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            Spacer(modifier = Modifier.width(1.dp))
        }
        
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .background(
                    Color.Black.copy(alpha = 0.3f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "关闭",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ImagePreviewBottomBar(
    imageUrl: String,
    onDownload: ((String) -> Unit)?,
    onShare: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color.Black.copy(alpha = 0.5f),
                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onDownload != null) {
            IconButton(
                onClick = { onDownload(imageUrl) },
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "下载",
                    tint = Color.White
                )
            }
        }
        
        if (onDownload != null && onShare != null) {
            Spacer(modifier = Modifier.width(24.dp))
        }
        
        if (onShare != null) {
            IconButton(
                onClick = { onShare(imageUrl) },
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "分享",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImagePreviewPreview() {
    ThemeProvider {
        // 预览占位
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "图片预览组件",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
