package com.xichen.matelink.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.xichen.matelink.core.ui.theme.ThemeProvider

/**
 * MateLink 图片组件集合
 * 基于Coil实现的各种图片显示组件
 */

/**
 * 用户头像组件
 */
@Composable
fun UserAvatar(
    avatarUrl: String?,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
    showOnlineStatus: Boolean = false,
    isOnline: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl ?: "android.resource://com.xichen.matelink/drawable/default_avatar")
                .size(Size.ORIGINAL) // 保持原始尺寸
                .transformations(CircleCropTransformation())
                .build(),
            contentDescription = "用户头像",
            modifier = Modifier
                .size(size)
                .then(
                    if (onClick != null) {
                        Modifier.clickable { onClick() }
                    } else {
                        Modifier
                    }
                ),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
            error = painterResource(android.R.drawable.ic_menu_report_image)
        )
        
        // 在线状态指示器
        if (showOnlineStatus) {
            Box(
                modifier = Modifier
                    .size(size * 0.3f)
                    .background(
                        color = if (isOnline) Color.Green else Color.Gray,
                        shape = CircleShape
                    )
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

/**
 * 聊天图片组件
 */
@Composable
fun ChatImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    maxWidth: Dp = 200.dp,
    maxHeight: Dp = 200.dp,
    cornerRadius: Dp = 8.dp,
    onClick: (() -> Unit)? = null
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(600, 600) // 限制加载尺寸
            .transformations(
                RoundedCornersTransformation(cornerRadius.value)
            )
            .build(),
        contentDescription = "聊天图片",
        modifier = modifier
            .widthIn(max = maxWidth)
            .heightIn(max = maxHeight)
            .clip(RoundedCornerShape(cornerRadius))
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
        error = painterResource(android.R.drawable.ic_menu_report_image)
    )
}

/**
 * 空间头像组件
 */
@Composable
fun SpaceAvatar(
    avatarUrl: String?,
    spaceName: String,
    size: Dp = 56.dp,
    shape: Shape = CircleShape,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(avatarUrl ?: generateDefaultSpaceAvatar(spaceName))
            .size(Size.ORIGINAL)
            .transformations(
                when (shape) {
                    is CircleShape -> listOf(CircleCropTransformation())
                    is RoundedCornerShape -> listOf(RoundedCornersTransformation(8f))
                    else -> emptyList()
                }
            )
            .build(),
        contentDescription = "空间头像",
        modifier = modifier
            .size(size)
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
        error = painterResource(android.R.drawable.ic_menu_report_image)
    )
}

/**
 * 朋友圈图片网格组件
 */
@Composable
fun MomentImageGrid(
    images: List<String>,
    modifier: Modifier = Modifier,
    maxImages: Int = 9,
    onImageClick: (Int, List<String>) -> Unit = { _, _ -> }
) {
    val displayImages = images.take(maxImages)
    
    when (displayImages.size) {
        0 -> {
            // 无图片
        }
        1 -> {
            // 单图显示
            SingleMomentImage(
                imageUrl = displayImages[0],
                modifier = modifier,
                onClick = { onImageClick(0, images) }
            )
        }
        else -> {
            // 多图网格显示
            MomentImageGridLayout(
                images = displayImages,
                allImages = images,
                modifier = modifier,
                onImageClick = onImageClick
            )
        }
    }
}

@Composable
private fun SingleMomentImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(800, 600) // 单图可以大一些
            .build(),
        contentDescription = "朋友圈图片",
        modifier = modifier
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentScale = ContentScale.Crop,
        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
        error = painterResource(android.R.drawable.ic_menu_report_image)
    )
}

@Composable
private fun MomentImageGridLayout(
    images: List<String>,
    allImages: List<String>,
    modifier: Modifier = Modifier,
    onImageClick: (Int, List<String>) -> Unit
) {
    val columns = when (images.size) {
        2 -> 2
        3 -> 3
        4 -> 2
        else -> 3
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.heightIn(max = 300.dp),
        userScrollEnabled = false
    ) {
        itemsIndexed(images) { index, imageUrl ->
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .size(300, 300) // 缩略图尺寸
                        .build(),
                    contentDescription = "朋友圈图片",
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onImageClick(index, allImages) },
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                    error = painterResource(android.R.drawable.ic_menu_report_image)
                )
                
                // 如果图片数量超过显示数量，显示"更多"标识
                if (index == images.size - 1 && allImages.size > images.size) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${allImages.size - images.size}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * 消息缩略图组件
 */
@Composable
fun MessageThumbnail(
    imageUrl: String,
    size: Dp = 60.dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(200, 200) // 缩略图尺寸
            .transformations(RoundedCornersTransformation(4f))
            .build(),
        contentDescription = "消息缩略图",
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(4.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
        error = painterResource(android.R.drawable.ic_menu_report_image)
    )
}

/**
 * 背景图片组件
 */
@Composable
fun BackgroundImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = 0.3f
) {
    if (imageUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .size(Size.ORIGINAL)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = "背景图片",
            modifier = modifier,
            contentScale = contentScale,
            alpha = alpha
        )
    }
}

/**
 * 图片加载状态组件
 */
@Composable
fun ImageWithLoadingState(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .listener(
                    onStart = { isLoading = true },
                    onSuccess = { _, _ -> 
                        isLoading = false
                        hasError = false
                    },
                    onError = { _, _ ->
                        isLoading = false
                        hasError = true
                    }
                )
                .build(),
            contentDescription = "图片",
            modifier = Modifier
                .fillMaxSize()
                .clip(shape),
            contentScale = contentScale
        )
        
        // 加载状态覆盖层
        if (isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
        
        // 错误状态覆盖层
        if (hasError) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                        shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_report_image),
                    contentDescription = "加载失败",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

/**
 * 自适应图片组件
 * 根据内存状态自动调整图片质量
 */
@Composable
fun AdaptiveImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val context = LocalContext.current
    val memoryUtils = remember { MemoryUtils(context) }
    val memoryStatus by remember { mutableStateOf(memoryUtils.getMemoryStatus()) }
    
    val imageSize = when (memoryStatus) {
        MemoryStatus.GOOD -> 800
        MemoryStatus.MODERATE -> 600
        MemoryStatus.HIGH -> 400
        MemoryStatus.CRITICAL -> 200
    }
    
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(imageSize, imageSize)
            .build(),
        contentDescription = "自适应图片",
        modifier = modifier.clip(shape),
        contentScale = contentScale,
        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
        error = painterResource(android.R.drawable.ic_menu_report_image)
    )
}

/**
 * 图片预加载组件
 */
@Composable
fun PreloadImage(imageUrl: String) {
    val context = LocalContext.current
    
    LaunchedEffect(imageUrl) {
        val imageLoader = coil.ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()
        imageLoader.execute(request)
    }
}

/**
 * 批量图片预加载
 */
@Composable
fun PreloadImages(imageUrls: List<String>) {
    val context = LocalContext.current
    
    LaunchedEffect(imageUrls) {
        val imageLoader = coil.ImageLoader(context)
        imageUrls.forEach { url ->
            val request = ImageRequest.Builder(context)
                .data(url)
                .size(300, 300) // 预加载缩略图
                .build()
            imageLoader.execute(request)
        }
    }
}

// ========== 工具函数 ==========

/**
 * 生成默认空间头像
 */
private fun generateDefaultSpaceAvatar(spaceName: String): String {
    // 可以根据空间名称生成默认头像
    // 这里返回一个占位符
    return "android.resource://com.xichen.matelink/drawable/default_space_avatar"
}

/**
 * 获取图片加载建议尺寸
 */
fun getRecommendedImageSize(memoryStatus: MemoryStatus): Int {
    return when (memoryStatus) {
        MemoryStatus.GOOD -> 1080
        MemoryStatus.MODERATE -> 720
        MemoryStatus.HIGH -> 480
        MemoryStatus.CRITICAL -> 240
    }
}

// ========== 预览 ==========

@Preview(showBackground = true)
@Composable
fun UserAvatarPreview() {
    ThemeProvider {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserAvatar(
                avatarUrl = null,
                size = 48.dp,
                showOnlineStatus = true,
                isOnline = true
            )
            
            UserAvatar(
                avatarUrl = null,
                size = 64.dp,
                showOnlineStatus = true,
                isOnline = false
            )
            
            UserAvatar(
                avatarUrl = null,
                size = 80.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MomentImageGridPreview() {
    ThemeProvider {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("单图显示")
            MomentImageGrid(
                images = listOf("https://example.com/image1.jpg")
            )
            
            Text("多图显示")
            MomentImageGrid(
                images = listOf(
                    "https://example.com/image1.jpg",
                    "https://example.com/image2.jpg",
                    "https://example.com/image3.jpg",
                    "https://example.com/image4.jpg"
                )
            )
        }
    }
}
