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
import com.xichen.matelink.core.network.api.*
import com.xichen.matelink.core.network.error.NetworkErrorHandler
import com.xichen.matelink.core.network.model.Result
import com.xichen.matelink.core.network.monitor.NetworkMonitor
import com.xichen.matelink.core.network.monitor.NetworkStatus
import com.xichen.matelink.core.network.utils.NetworkUtils
import com.xichen.matelink.core.ui.theme.ThemeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 网络功能演示页面
 */
@Composable
fun NetworkDemo(
    viewModel: NetworkDemoViewModel = androidx.lifecycle.viewmodel.compose.hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val networkStatus by viewModel.networkStatus.collectAsState()
    val loginResult by viewModel.loginResult.collectAsState()
    val spaces by viewModel.spaces.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 标题
        Text(
            text = "网络功能演示",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // 网络状态显示
        NetworkStatusCard(networkStatus)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 登录演示
        LoginDemoCard(
            loginResult = loginResult,
            onLoginClick = viewModel::demoLogin
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 空间列表演示
        SpaceListCard(
            spaces = spaces,
            onRefreshClick = viewModel::loadSpaces
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 网络工具演示
        NetworkUtilsCard(viewModel)
    }
}

@Composable
fun NetworkStatusCard(networkStatus: NetworkStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (networkStatus) {
                NetworkStatus.CONNECTED_WIFI,
                NetworkStatus.CONNECTED_ETHERNET -> MaterialTheme.colorScheme.primaryContainer
                NetworkStatus.CONNECTED_MOBILE -> MaterialTheme.colorScheme.secondaryContainer
                NetworkStatus.CONNECTED_NO_INTERNET -> MaterialTheme.colorScheme.errorContainer
                NetworkStatus.DISCONNECTED -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 状态指示器
            Card(
                modifier = Modifier.size(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (networkStatus) {
                        NetworkStatus.CONNECTED_WIFI,
                        NetworkStatus.CONNECTED_ETHERNET,
                        NetworkStatus.CONNECTED_MOBILE -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    }
                ),
                shape = RoundedCornerShape(6.dp)
            ) {}
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = "网络状态",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getNetworkStatusText(networkStatus),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun LoginDemoCard(
    loginResult: Result<LoginResponse>?,
    onLoginClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "登录API演示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (loginResult) {
                is Result.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("登录中...")
                    }
                }
                is Result.Success -> {
                    Text(
                        text = "登录成功！用户：${loginResult.data.user.username}",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is Result.Error -> {
                    Text(
                        text = "登录失败：${loginResult.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                null -> {
                    Text("点击按钮测试登录API")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = loginResult !is Result.Loading
            ) {
                Text("测试登录API")
            }
        }
    }
}

@Composable
fun SpaceListCard(
    spaces: Result<List<Space>>?,
    onRefreshClick: () -> Unit
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
                    text = "空间列表API演示",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onRefreshClick) {
                    Text("刷新")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (spaces) {
                is Result.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("加载中...")
                    }
                }
                is Result.Success -> {
                    if (spaces.data.isEmpty()) {
                        Text("暂无空间数据")
                    } else {
                        Column {
                            Text("加载成功，共 ${spaces.data.size} 个空间：")
                            spaces.data.take(3).forEach { space ->
                                Text(
                                    text = "• ${space.name} (${space.memberCount}人)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }
                    }
                }
                is Result.Error -> {
                    Text(
                        text = "加载失败：${spaces.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                null -> {
                    Text("点击刷新按钮测试空间API")
                }
            }
        }
    }
}

@Composable
fun NetworkUtilsCard(viewModel: NetworkDemoViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "网络工具演示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 缓存信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("缓存大小:")
                Text("${viewModel.getCacheSize()}")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 清除缓存按钮
            Button(
                onClick = viewModel::clearCache,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("清除缓存")
            }
        }
    }
}

// ========== ViewModel ==========

@HiltViewModel
class NetworkDemoViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val spaceApi: SpaceApi,
    private val networkMonitor: NetworkMonitor,
    private val networkUtils: NetworkUtils
) : ViewModel() {
    
    private val _loginResult = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginResult: StateFlow<Result<LoginResponse>?> = _loginResult.asStateFlow()
    
    private val _spaces = MutableStateFlow<Result<List<Space>>?>(null)
    val spaces: StateFlow<Result<List<Space>>?> = _spaces.asStateFlow()
    
    val networkStatus = networkMonitor.networkStatus
    
    /**
     * 演示登录
     */
    fun demoLogin() {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            
            val result = networkUtils.safeApiCall {
                retrofit2.Response.success(
                    LoginResponse(
                        user = UserProfile(
                            id = "demo_user",
                            email = "demo@matelink.com",
                            username = "演示用户",
                            createdAt = "2024-01-01 00:00:00",
                            updatedAt = "2024-01-01 00:00:00"
                        ),
                        accessToken = "demo_access_token",
                        refreshToken = "demo_refresh_token",
                        expiresIn = 3600
                    )
                )
            }
            
            _loginResult.value = result
        }
    }
    
    /**
     * 加载空间列表
     */
    fun loadSpaces() {
        viewModelScope.launch {
            _spaces.value = Result.Loading
            
            val result = networkUtils.safeApiCall {
                retrofit2.Response.success(
                    listOf(
                        Space(
                            id = "space1",
                            name = "好友群",
                            description = "老同学聚会群",
                            type = SpaceType.GROUP,
                            memberCount = 15,
                            isOwner = true,
                            role = MemberRole.OWNER,
                            createdAt = "2024-01-01 00:00:00",
                            updatedAt = "2024-01-01 00:00:00"
                        ),
                        Space(
                            id = "space2",
                            name = "工作交流",
                            description = "项目讨论组",
                            type = SpaceType.GROUP,
                            memberCount = 8,
                            isOwner = false,
                            role = MemberRole.MEMBER,
                            createdAt = "2024-01-01 00:00:00",
                            updatedAt = "2024-01-01 00:00:00"
                        )
                    )
                )
            }
            
            _spaces.value = result
        }
    }
    
    /**
     * 获取缓存大小
     */
    fun getCacheSize(): String {
        return "2.5 MB" // 演示数据
    }
    
    /**
     * 清除缓存
     */
    fun clearCache() {
        // 演示清除缓存
    }
}

// ========== 工具函数 ==========

fun getNetworkStatusText(status: NetworkStatus): String {
    return when (status) {
        NetworkStatus.CONNECTED_WIFI -> "WiFi 已连接"
        NetworkStatus.CONNECTED_MOBILE -> "移动网络 已连接"
        NetworkStatus.CONNECTED_ETHERNET -> "以太网 已连接"
        NetworkStatus.CONNECTED_NO_INTERNET -> "已连接但无互联网"
        NetworkStatus.DISCONNECTED -> "网络未连接"
    }
}

@Preview(showBackground = true)
@Composable
fun NetworkDemoPreview() {
    ThemeProvider {
        // NetworkDemo() // 需要注入依赖，预览时注释掉
        Text("网络演示页面")
    }
}
