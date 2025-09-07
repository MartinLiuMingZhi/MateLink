package com.xichen.matelink.core.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.xichen.matelink.core.ui.image.ImageLoaderManager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 图片预览组件
 * 支持缩放、拖拽、滑动查看多张图片
 */

/**
 * 单张图片预览
 */
@Composable
fun ImagePreviewDialog(
    imageUrl: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoaderManager: ImageLoaderManager? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        ImagePreviewContent(
            imageUrl = imageUrl,
            onDismiss = onDismiss,
            modifier = modifier,
            imageLoaderManager = imageLoaderManager
        )
    }
}

/**
 * 多张图片预览
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MultiImagePreviewDialog(
    imageUrls: List<String>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoaderManager: ImageLoaderManager? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        MultiImagePreviewContent(
            imageUrls = imageUrls,
            initialIndex = initialIndex,
            onDismiss = onDismiss,
            modifier = modifier,
            imageLoaderManager = imageLoaderManager
        )
    }
}

@Composable
private fun ImagePreviewContent(
    imageUrl: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoaderManager: ImageLoaderManager? = null
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    
    val context = LocalContext.current
    val imageLoader = imageLoaderManager?.getImageLoader() ?: coil.ImageLoader(context)
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 关闭按钮
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "关闭",
                tint = Color.White
            )
        }
        
        // 图片内容
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 3f)
                        offsetX = (offsetX + pan.x).coerceIn(-500f, 500f)
                        offsetY = (offsetY + pan.y).coerceIn(-500f, 500f)
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            contentScale = ContentScale.Fit,
            imageLoader = imageLoader
        )
        
        // 双击重置
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        }
                    )
                }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MultiImagePreviewContent(
    imageUrls: List<String>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoaderManager: ImageLoaderManager? = null
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { imageUrls.size }
    )
    
    val context = LocalContext.current
    val imageLoader = imageLoaderManager?.getImageLoader() ?: coil.ImageLoader(context)
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 关闭按钮
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "关闭",
                tint = Color.White
            )
        }
        
        // 图片指示器
        if (imageUrls.size > 1) {
            Text(
                text = "${pagerState.currentPage + 1} / ${imageUrls.size}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        
        // 图片内容
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            var scale by remember { mutableStateOf(1f) }
            var offsetX by remember { mutableStateOf(0f) }
            var offsetY by remember { mutableStateOf(0f) }
            
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrls[page])
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.5f, 3f)
                            offsetX = (offsetX + pan.x).coerceIn(-500f, 500f)
                            offsetY = (offsetY + pan.y).coerceIn(-500f, 500f)
                        }
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                contentScale = ContentScale.Fit,
                imageLoader = imageLoader
            )
            
            // 双击重置
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            }
                        )
                    }
            )
        }
    }
}

/**
 * 图片预览卡片
 */
@Composable
fun ImagePreviewCard(
    imageUrl: String,
    title: String? = null,
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit = {},
    imageLoaderManager: ImageLoaderManager? = null
) {
    Card(
        modifier = modifier.clickable { onImageClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                imageLoader = imageLoaderManager?.getImageLoader() ?: coil.ImageLoader(LocalContext.current)
            )
            
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 图片网格预览
 */
@Composable
fun ImageGridPreview(
    imageUrls: List<String>,
    columns: Int = 3,
    modifier: Modifier = Modifier,
    onImageClick: (Int) -> Unit = {},
    imageLoaderManager: ImageLoaderManager? = null
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(imageUrls.size) { index ->
            AsyncImage(
                model = imageUrls[index],
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable { onImageClick(index) },
                contentScale = ContentScale.Crop,
                imageLoader = imageLoaderManager?.getImageLoader() ?: coil.ImageLoader(LocalContext.current)
            )
        }
    }
}
