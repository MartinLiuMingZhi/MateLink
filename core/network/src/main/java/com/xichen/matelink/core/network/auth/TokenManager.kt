package com.xichen.matelink.core.network.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.xichen.matelink.core.network.interceptor.TokenProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Token管理器
 * 负责token的存储、获取和刷新
 */

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

@Singleton
class TokenManager @Inject constructor(
    private val context: Context,
    private val authApi: AuthApi
) : TokenProvider {
    
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val TOKEN_EXPIRE_TIME_KEY = stringPreferencesKey("token_expire_time")
    }
    
    override suspend fun getAccessToken(): String? {
        return context.tokenDataStore.data
            .map { preferences -> preferences[ACCESS_TOKEN_KEY] }
            .first()
    }
    
    override suspend fun getRefreshToken(): String? {
        return context.tokenDataStore.data
            .map { preferences -> preferences[REFRESH_TOKEN_KEY] }
            .first()
    }
    
    override suspend fun refreshToken(): String? {
        val refreshToken = getRefreshToken() ?: return null
        
        return try {
            val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.success && response.data != null) {
                val tokenData = response.data
                saveTokens(tokenData.accessToken, tokenData.refreshToken)
                tokenData.accessToken
            } else {
                clearTokens()
                null
            }
        } catch (e: Exception) {
            clearTokens()
            null
        }
    }
    
    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.tokenDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[TOKEN_EXPIRE_TIME_KEY] = System.currentTimeMillis().toString()
        }
    }
    
    override suspend fun clearTokens() {
        context.tokenDataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(TOKEN_EXPIRE_TIME_KEY)
        }
    }
    
    /**
     * 检查token是否过期
     */
    suspend fun isTokenExpired(): Boolean {
        val expireTime = context.tokenDataStore.data
            .map { preferences -> preferences[TOKEN_EXPIRE_TIME_KEY]?.toLongOrNull() ?: 0L }
            .first()
        
        // 假设token有效期为24小时
        val tokenValidDuration = 24 * 60 * 60 * 1000L
        return System.currentTimeMillis() - expireTime > tokenValidDuration
    }
    
    /**
     * 检查是否已登录
     */
    suspend fun isLoggedIn(): Boolean {
        return getAccessToken() != null && !isTokenExpired()
    }
}

/**
 * 认证API接口
 */
interface AuthApi {
    suspend fun refreshToken(request: RefreshTokenRequest): ApiResponse<TokenData>
}

/**
 * 刷新token请求
 */
data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * Token数据
 */
data class TokenData(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

/**
 * API响应基类
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val code: Int = 0
)
