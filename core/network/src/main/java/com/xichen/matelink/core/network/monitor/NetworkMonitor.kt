package com.xichen.matelink.core.network.monitor

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 网络状态监控器
 * 监控网络连接状态变化
 */
@Singleton
class NetworkMonitor @Inject constructor(
    private val context: Context
) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    /**
     * 网络状态流
     */
    val networkStatus: Flow<NetworkStatus> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                val status = getNetworkStatus(capabilities)
                trySend(status)
            }
            
            override fun onLost(network: Network) {
                trySend(NetworkStatus.DISCONNECTED)
            }
            
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val status = getNetworkStatus(networkCapabilities)
                trySend(status)
            }
        }
        
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        
        // 发送当前网络状态
        trySend(getCurrentNetworkStatus())
        
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged()
    
    /**
     * 获取当前网络状态
     */
    fun getCurrentNetworkStatus(): NetworkStatus {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return getNetworkStatus(capabilities)
    }
    
    /**
     * 根据网络能力获取网络状态
     */
    private fun getNetworkStatus(capabilities: NetworkCapabilities?): NetworkStatus {
        if (capabilities == null) {
            return NetworkStatus.DISCONNECTED
        }
        
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    NetworkStatus.CONNECTED_WIFI
                } else {
                    NetworkStatus.CONNECTED_NO_INTERNET
                }
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    NetworkStatus.CONNECTED_MOBILE
                } else {
                    NetworkStatus.CONNECTED_NO_INTERNET
                }
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                NetworkStatus.CONNECTED_ETHERNET
            }
            else -> NetworkStatus.DISCONNECTED
        }
    }
    
    /**
     * 检查是否有网络连接
     */
    fun isConnected(): Boolean {
        return getCurrentNetworkStatus() != NetworkStatus.DISCONNECTED
    }
    
    /**
     * 检查是否为WiFi连接
     */
    fun isWifiConnected(): Boolean {
        return getCurrentNetworkStatus() == NetworkStatus.CONNECTED_WIFI
    }
    
    /**
     * 检查是否为移动网络连接
     */
    fun isMobileConnected(): Boolean {
        return getCurrentNetworkStatus() == NetworkStatus.CONNECTED_MOBILE
    }
    
    /**
     * 获取网络类型描述
     */
    fun getNetworkTypeDescription(): String {
        return when (getCurrentNetworkStatus()) {
            NetworkStatus.CONNECTED_WIFI -> "WiFi"
            NetworkStatus.CONNECTED_MOBILE -> "移动网络"
            NetworkStatus.CONNECTED_ETHERNET -> "以太网"
            NetworkStatus.CONNECTED_NO_INTERNET -> "已连接但无互联网"
            NetworkStatus.DISCONNECTED -> "未连接"
        }
    }
}

/**
 * 网络状态枚举
 */
enum class NetworkStatus {
    CONNECTED_WIFI,         // WiFi连接
    CONNECTED_MOBILE,       // 移动网络连接
    CONNECTED_ETHERNET,     // 以太网连接
    CONNECTED_NO_INTERNET,  // 已连接但无互联网
    DISCONNECTED            // 未连接
}

/**
 * 网络质量监控器
 */
@Singleton
class NetworkQualityMonitor @Inject constructor(
    private val networkMonitor: NetworkMonitor
) {
    
    /**
     * 网络质量评估
     */
    fun assessNetworkQuality(): NetworkQuality {
        val status = networkMonitor.getCurrentNetworkStatus()
        
        return when (status) {
            NetworkStatus.CONNECTED_WIFI -> NetworkQuality.EXCELLENT
            NetworkStatus.CONNECTED_ETHERNET -> NetworkQuality.EXCELLENT
            NetworkStatus.CONNECTED_MOBILE -> {
                // 可以根据信号强度等进一步判断
                NetworkQuality.GOOD
            }
            NetworkStatus.CONNECTED_NO_INTERNET -> NetworkQuality.POOR
            NetworkStatus.DISCONNECTED -> NetworkQuality.NO_CONNECTION
        }
    }
    
    /**
     * 获取建议的请求策略
     */
    fun getRecommendedRequestStrategy(): RequestStrategy {
        return when (assessNetworkQuality()) {
            NetworkQuality.EXCELLENT -> RequestStrategy.NORMAL
            NetworkQuality.GOOD -> RequestStrategy.OPTIMIZED
            NetworkQuality.POOR -> RequestStrategy.CONSERVATIVE
            NetworkQuality.NO_CONNECTION -> RequestStrategy.OFFLINE_ONLY
        }
    }
}

/**
 * 网络质量枚举
 */
enum class NetworkQuality {
    EXCELLENT,      // 优秀
    GOOD,           // 良好
    POOR,           // 较差
    NO_CONNECTION   // 无连接
}

/**
 * 请求策略枚举
 */
enum class RequestStrategy {
    NORMAL,         // 正常策略
    OPTIMIZED,      // 优化策略（压缩图片等）
    CONSERVATIVE,   // 保守策略（只加载必要内容）
    OFFLINE_ONLY    // 仅离线内容
}
