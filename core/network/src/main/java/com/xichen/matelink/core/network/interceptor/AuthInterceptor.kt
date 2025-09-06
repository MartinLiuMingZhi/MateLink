package com.xichen.matelink.core.network.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 认证拦截器
 * 自动为请求添加认证token
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 检查是否需要认证
        if (shouldSkipAuth(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }
        
        // 获取token
        val token = runBlocking {
            tokenProvider.getAccessToken()
        }
        
        // 添加认证头
        val authenticatedRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        val response = chain.proceed(authenticatedRequest)
        
        // 处理token过期
        if (response.code == 401 && token != null) {
            response.close()
            return handleTokenExpired(chain, originalRequest)
        }
        
        return response
    }
    
    /**
     * 判断是否跳过认证
     */
    private fun shouldSkipAuth(path: String): Boolean {
        val skipPaths = listOf(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/public/"
        )
        return skipPaths.any { path.contains(it) }
    }
    
    /**
     * 处理token过期
     */
    private fun handleTokenExpired(chain: Interceptor.Chain, originalRequest: okhttp3.Request): Response {
        return runBlocking {
            try {
                // 尝试刷新token
                val newToken = tokenProvider.refreshToken()
                if (newToken != null) {
                    // 使用新token重新请求
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                    chain.proceed(newRequest)
                } else {
                    // 刷新失败，返回401响应
                    chain.proceed(originalRequest)
                }
            } catch (e: Exception) {
                // 刷新token失败，返回原始响应
                chain.proceed(originalRequest)
            }
        }
    }
}

/**
 * Token提供器接口
 */
interface TokenProvider {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun refreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
}
