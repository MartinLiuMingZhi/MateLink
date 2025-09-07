package com.xichen.matelink.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.xichen.matelink.core.ui.image.ImageLoaderManager
import com.xichen.matelink.core.ui.image.ImageUtils
import javax.inject.Inject

/**
 * 图片组件集合
 * 提供各种常用的图片显示组件
 */

/**
 * 基础图片组件
 */
@Composable
fun BaseImage(
    data: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter? = null,
    error: Painter? = null,
    onLoading: @Composable (() -> Unit)? = null,
    onError: @Composable (() -> Unit)? = null,
    imageLoaderManager: ImageLoaderManager? = null
) {
    val context = LocalContext.current
    val imageLoader = imageLoaderManager?.getImageLoader() ?: coil.ImageLoader(context)
    
    AsyncImage(
        model = data,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = placeholder,
        error = error,
        imageLoader = imageLoader
    )
}

/**
 * 圆形图片组件
 */
@Composable
fun CircleImage(
    data: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    placeholder: Painter? = null,
    error: Painter? = null,
    imageLoaderManager: ImageLoaderManager? = null
) {
    BaseImage(
        data = data,
        contentDescription = contentDescription,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (borderWidth > 0.dp) {
                    Modifier.border(borderWidth, borderColor, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentScale = ContentScale.Crop,
        placeholder = placeholder,
        error = error,
        imageLoaderManager = imageLoaderManager
    )
}

/**
 * 圆角图片组件
 */
@Composable
fun RoundedImage(
    data: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter? = null,
    error: Painter? = null,
    imageLoaderManager: ImageLoaderManager? = null
) {
    BaseImage(
        data = data,
        contentDescription = contentDescription,
        modifier = modifier.clip(RoundedCornerShape(cornerRadius)),
        contentScale = contentScale,
        placeholder = placeholder,
        error = error,
        imageLoaderManager = imageLoaderManager
    )
}

/**
 * 自定义形状图片组件
 */
@Composable
fun ShapedImage(
    data: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter? = null,
    error: Painter? = null,
    imageLoaderManager: ImageLoaderManager? = null
) {
    BaseImage(
        data = data,
        contentDescription = contentDescription,
        modifier = modifier.clip(shape),
        contentScale = contentScale,
        placeholder = placeholder,
        error = error,
        imageLoaderManager = imageLoaderManager
    )
}

/**
 * 带加载状态的图片组件
 */
@Composable
fun LoadingImage(
    data: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    imageLoaderManager: ImageLoaderManager? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        BaseImage(
            data = data,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
            imageLoaderManager = imageLoaderManager,
            onLoading = {
                isLoading = true
                hasError = false
            },
            onError = {
                isLoading = false
                hasError = true
            }
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
        
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "加载失败",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 可点击的图片组件
 */
@Composable
fun ClickableImage(
    data: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: () -> Unit,
    imageLoaderManager: ImageLoaderManager? = null
) {
    BaseImage(
        data = data,
        contentDescription = contentDescription,
        modifier = modifier.clickable { onClick() },
        contentScale = contentScale,
        imageLoaderManager = imageLoaderManager
    )
}

/**
 * 头像组件
 */
@Composable
fun AvatarImage(
    data: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    borderWidth: Dp = 2.dp,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    placeholder: Painter? = null,
    imageLoaderManager: ImageLoaderManager? = null
) {
    CircleImage(
        data = data,
        contentDescription = contentDescription,
        modifier = modifier,
        size = size,
        borderWidth = borderWidth,
        borderColor = borderColor,
        placeholder = placeholder,
        imageLoaderManager = imageLoaderManager
    )
}

/**
 * 缩略图组件
 */
@Composable
fun ThumbnailImage(
    data: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    cornerRadius: Dp = 4.dp,
    imageLoaderManager: ImageLoaderManager? = null
) {
    RoundedImage(
        data = data,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        cornerRadius = cornerRadius,
        imageLoaderManager = imageLoaderManager
    )
}

/**
 * 横幅图片组件
 */
@Composable
fun BannerImage(
    data: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    cornerRadius: Dp = 8.dp,
    imageLoaderManager: ImageLoaderManager? = null
) {
    RoundedImage(
        data = data,
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        cornerRadius = cornerRadius,
        contentScale = ContentScale.Crop,
        imageLoaderManager = imageLoaderManager
    )
}

/**
 * 网格图片组件
 */
@Composable
fun GridImage(
    data: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1f,
    cornerRadius: Dp = 4.dp,
    imageLoaderManager: ImageLoaderManager? = null
) {
    RoundedImage(
        data = data,
        contentDescription = contentDescription,
        modifier = modifier.aspectRatio(aspectRatio),
        cornerRadius = cornerRadius,
        contentScale = ContentScale.Crop,
        imageLoaderManager = imageLoaderManager
    )
}
