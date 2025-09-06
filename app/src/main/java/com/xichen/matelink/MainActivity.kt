package com.xichen.matelink

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xichen.matelink.core.ui.base.BaseActivity
import com.xichen.matelink.core.ui.component.AdaptiveLayout
import com.xichen.matelink.core.ui.component.ResponsiveSpacer
import com.xichen.matelink.core.ui.component.responsivePadding
import com.xichen.matelink.core.ui.theme.ThemeProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    
    @Composable
    override fun Content() {
        ThemeProvider {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    AdaptiveLayout(
        modifier = Modifier
            .fillMaxSize()
            .responsivePadding(),
        compactContent = {
            CompactLayout()
        },
        mediumContent = {
            MediumLayout()
        },
        expandedContent = {
            ExpandedLayout()
        }
    )
}

@Composable
fun CompactLayout() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MateLink - 友联",
            style = MaterialTheme.typography.headlineMedium
        )
        ResponsiveSpacer()
        Text(
            text = "紧凑布局 - 手机模式",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun MediumLayout() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MateLink - 友联",
                style = MaterialTheme.typography.headlineLarge
            )
            ResponsiveSpacer()
            Text(
                text = "中等布局 - 平板模式",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ExpandedLayout() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MateLink - 友联",
                style = MaterialTheme.typography.displayMedium
            )
            ResponsiveSpacer()
            Text(
                text = "展开布局 - 大屏模式",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun CompactPreview() {
    ThemeProvider {
        CompactLayout()
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
fun MediumPreview() {
    ThemeProvider {
        MediumLayout()
    }
}

@Preview(showBackground = true, widthDp = 1200)
@Composable
fun ExpandedPreview() {
    ThemeProvider {
        ExpandedLayout()
    }
}

