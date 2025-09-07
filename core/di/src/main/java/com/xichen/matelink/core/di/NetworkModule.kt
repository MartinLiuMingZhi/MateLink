package com.xichen.matelink.core.di

import android.content.Context
import com.xichen.matelink.core.network.ApiClient
import com.xichen.matelink.core.network.api.AuthApi
import com.xichen.matelink.core.network.api.ChatApi
import com.xichen.matelink.core.network.api.MomentApi
import com.xichen.matelink.core.network.api.SpaceApi
import com.xichen.matelink.core.network.auth.TokenManager
import com.xichen.matelink.core.network.error.NetworkErrorHandler
import com.xichen.matelink.core.network.interceptor.*
import com.xichen.matelink.core.network.monitor.NetworkMonitor
import com.xichen.matelink.core.network.monitor.NetworkQualityMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 网络模块依赖注入配置
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    /**
     * 提供认证拦截器
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenProvider: TokenProvider): AuthInterceptor {
        return AuthInterceptor(tokenProvider)
    }
    
    /**
     * 提供日志拦截器
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): LoggingInterceptor {
        return LoggingInterceptor()
    }
    
    /**
     * 提供网络拦截器
     */
    @Provides
    @Singleton
    fun provideNetworkInterceptor(@ApplicationContext context: Context): NetworkInterceptor {
        return NetworkInterceptor(context)
    }
    
    /**
     * 提供通用头部拦截器
     */
    @Provides
    @Singleton
    fun provideCommonHeaderInterceptor(): CommonHeaderInterceptor {
        return CommonHeaderInterceptor()
    }
    
    /**
     * 提供缓存拦截器
     */
    @Provides
    @Singleton
    fun provideCacheInterceptor(): CacheInterceptor {
        return CacheInterceptor()
    }
    
    /**
     * 提供重试拦截器
     */
    @Provides
    @Singleton
    fun provideRetryInterceptor(): RetryInterceptor {
        return RetryInterceptor()
    }
    
    /**
     * 提供Token管理器
     */
    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context,
        authApi: AuthApi
    ): TokenManager {
        return TokenManager(context, authApi)
    }
    
    /**
     * 提供TokenProvider
     */
    @Provides
    @Singleton
    fun provideTokenProvider(tokenManager: TokenManager): TokenProvider {
        return tokenManager
    }
    
    // OkHttpClient provider temporarily removed to resolve dependency issues
    
    /**
     * 提供API客户端
     */
    @Provides
    @Singleton
    fun provideApiClient(
        @ApplicationContext context: Context,
        authInterceptor: AuthInterceptor,
        loggingInterceptor: LoggingInterceptor,
        networkInterceptor: NetworkInterceptor,
        commonHeaderInterceptor: CommonHeaderInterceptor,
        cacheInterceptor: CacheInterceptor,
        retryInterceptor: RetryInterceptor
    ): ApiClient {
        return ApiClient(
            context = context,
            authInterceptor = authInterceptor,
            loggingInterceptor = loggingInterceptor,
            networkInterceptor = networkInterceptor,
            commonHeaderInterceptor = commonHeaderInterceptor,
            cacheInterceptor = cacheInterceptor,
            retryInterceptor = retryInterceptor
        )
    }
    
    /**
     * 提供认证API
     */
    @Provides
    @Singleton
    fun provideAuthApi(apiClient: ApiClient): AuthApi {
        return apiClient.createService()
    }
    
    /**
     * 提供空间API
     */
    @Provides
    @Singleton
    fun provideSpaceApi(apiClient: ApiClient): SpaceApi {
        return apiClient.createService()
    }
    
    /**
     * 提供聊天API
     */
    @Provides
    @Singleton
    fun provideChatApi(apiClient: ApiClient): ChatApi {
        return apiClient.createService()
    }
    
    /**
     * 提供朋友圈API
     */
    @Provides
    @Singleton
    fun provideMomentApi(apiClient: ApiClient): MomentApi {
        return apiClient.createService()
    }
    
    /**
     * 提供网络错误处理器
     */
    @Provides
    @Singleton
    fun provideNetworkErrorHandler(): NetworkErrorHandler {
        return NetworkErrorHandler()
    }
    
    /**
     * 提供网络监控器
     */
    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }
    
    /**
     * 提供网络质量监控器
     */
    @Provides
    @Singleton
    fun provideNetworkQualityMonitor(networkMonitor: NetworkMonitor): NetworkQualityMonitor {
        return NetworkQualityMonitor(networkMonitor)
    }
}
