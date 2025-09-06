package com.xichen.matelink.core.network.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 网络状态拦截器
 * 检查网络连接状态，无网络时抛出异常
 */
@Singleton
class NetworkInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkAvailable()) {
            throw NoNetworkException("No network available")
        }
        
        return chain.proceed(chain.request())
    }
    
    /**
     * 检查网络是否可用
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

/**
 * 无网络异常
 */
class NoNetworkException(message: String) : IOException(message)

/**
 * 通用网络拦截器
 * 添加通用请求头
 */
@Singleton
class CommonHeaderInterceptor @Inject constructor() : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val newRequest = originalRequest.newBuilder()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("User-Agent", getUserAgent())
            .header("App-Version", getAppVersion())
            .header("Platform", "Android")
            .build()
        
        return chain.proceed(newRequest)
    }
    
    /**
     * 获取用户代理
     */
    private fun getUserAgent(): String {
        return "MateLink/1.0.0 (Android ${android.os.Build.VERSION.RELEASE}; ${android.os.Build.MODEL})"
    }
    
    /**
     * 获取应用版本
     */
    private fun getAppVersion(): String {
        return "1.0.0" // 可以从BuildConfig获取
    }
}

/**
 * 缓存拦截器
 * 处理网络请求缓存
 */
@Singleton
class CacheInterceptor @Inject constructor() : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // 为GET请求添加缓存控制
        val newRequest = if (request.method == "GET") {
            request.newBuilder()
                .header("Cache-Control", "public, max-age=300") // 5分钟缓存
                .build()
        } else {
            request
        }
        
        val response = chain.proceed(newRequest)
        
        // 为响应添加缓存头
        return if (request.method == "GET") {
            response.newBuilder()
                .header("Cache-Control", "public, max-age=300")
                .removeHeader("Pragma")
                .build()
        } else {
            response
        }
    }
}

/**
 * 重试拦截器
 * 自动重试失败的请求
 */
@Singleton
class RetryInterceptor @Inject constructor() : Interceptor {
    
    private val maxRetryCount = 3
    private val retryDelay = 1000L // 1秒
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        var retryCount = 0
        
        while (!response.isSuccessful && retryCount < maxRetryCount && shouldRetry(response.code)) {
            response.close()
            retryCount++
            
            try {
                Thread.sleep(retryDelay * retryCount) // 递增延迟
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
            
            response = chain.proceed(request)
        }
        
        return response
    }
    
    /**
     * 判断是否应该重试
     */
    private fun shouldRetry(code: Int): Boolean {
        return when (code) {
            408, // Request Timeout
            429, // Too Many Requests
            500, // Internal Server Error
            502, // Bad Gateway
            503, // Service Unavailable
            504  // Gateway Timeout
            -> true
            else -> false
        }
    }
}
