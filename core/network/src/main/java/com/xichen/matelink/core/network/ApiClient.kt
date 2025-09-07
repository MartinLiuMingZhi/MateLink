package com.xichen.matelink.core.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.xichen.matelink.core.common.constants.AppConstants
import com.xichen.matelink.core.network.interceptor.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API客户端配置
 * 统一管理网络请求配置
 */
@Singleton
class ApiClient @Inject constructor(
    private val context: Context,
    private val authInterceptor: AuthInterceptor,
    private val loggingInterceptor: LoggingInterceptor,
    private val networkInterceptor: NetworkInterceptor,
    private val commonHeaderInterceptor: CommonHeaderInterceptor,
    private val cacheInterceptor: CacheInterceptor,
    private val retryInterceptor: RetryInterceptor
) {
    
    /**
     * Gson配置
     */
    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .serializeNulls()
        .create()
    
    /**
     * 缓存配置
     */
    private val cache: Cache = Cache(
        directory = File(context.cacheDir, "http_cache"),
        maxSize = 50L * 1024L * 1024L // 50MB
    )
    
    /**
     * OkHttp客户端配置
     */
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        // 超时配置
        .connectTimeout(AppConstants.Api.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(AppConstants.Api.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(AppConstants.Api.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        
        // 缓存配置
        .cache(cache)
        
        // 拦截器配置（顺序很重要）
        .addInterceptor(commonHeaderInterceptor)    // 通用请求头
        .addInterceptor(authInterceptor)            // 认证
        .addInterceptor(cacheInterceptor)           // 缓存控制
        .addInterceptor(retryInterceptor)           // 重试机制
        .addNetworkInterceptor(networkInterceptor)  // 网络检查
        .addNetworkInterceptor(loggingInterceptor)  // 日志记录
        
        .build()
    
    /**
     * Retrofit实例
     */
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(getBaseUrl())
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    /**
     * 获取基础URL
     */
    private fun getBaseUrl(): String {
        return if (BuildConfig.DEBUG) {
            "https://dev-api.matelink.com/"
        } else {
            "https://api.matelink.com/"
        }
    }
    
    /**
     * 创建API服务
     */
    inline fun <reified T> createService(): T {
        return retrofit.create(T::class.java)
    }
    
    /**
     * 获取缓存大小
     */
    fun getCacheSize(): Long {
        return cache.size()
    }
    
    /**
     * 清除缓存
     */
    fun clearCache() {
        try {
            cache.evictAll()
        } catch (e: Exception) {
            // 忽略清除缓存失败
        }
    }
    
    /**
     * 获取OkHttpClient实例
     */
    fun getOkHttpClient(): OkHttpClient {
        return okHttpClient
    }
}
