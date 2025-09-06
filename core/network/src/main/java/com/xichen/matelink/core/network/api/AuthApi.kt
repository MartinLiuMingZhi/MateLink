package com.xichen.matelink.core.network.api

import com.xichen.matelink.core.network.auth.ApiResponse
import com.xichen.matelink.core.network.auth.RefreshTokenRequest
import com.xichen.matelink.core.network.auth.TokenData
import retrofit2.http.*

/**
 * 认证相关API接口
 */
interface AuthApi {
    
    /**
     * 用户登录
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>
    
    /**
     * 用户注册
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<RegisterResponse>
    
    /**
     * 刷新token
     */
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<TokenData>
    
    /**
     * 退出登录
     */
    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>
    
    /**
     * 发送验证码
     */
    @POST("auth/send-code")
    suspend fun sendVerificationCode(@Body request: SendCodeRequest): ApiResponse<Unit>
    
    /**
     * 验证邮箱
     */
    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): ApiResponse<Unit>
    
    /**
     * 重置密码
     */
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ApiResponse<Unit>
    
    /**
     * 修改密码
     */
    @PUT("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ApiResponse<Unit>
    
    /**
     * 获取用户信息
     */
    @GET("auth/profile")
    suspend fun getProfile(): ApiResponse<UserProfile>
    
    /**
     * 更新用户信息
     */
    @PUT("auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<UserProfile>
}

// ========== 请求数据类 ==========

data class LoginRequest(
    val email: String,
    val password: String,
    val deviceId: String? = null,
    val deviceType: String = "android"
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    val verificationCode: String,
    val deviceId: String? = null
)

data class SendCodeRequest(
    val email: String,
    val type: CodeType = CodeType.REGISTER
)

enum class CodeType {
    REGISTER,           // 注册验证码
    RESET_PASSWORD,     // 重置密码验证码
    VERIFY_EMAIL        // 邮箱验证码
}

data class VerifyEmailRequest(
    val email: String,
    val verificationCode: String
)

data class ResetPasswordRequest(
    val email: String,
    val verificationCode: String,
    val newPassword: String
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

data class UpdateProfileRequest(
    val username: String? = null,
    val avatar: String? = null,
    val signature: String? = null,
    val birthday: String? = null,
    val gender: Int? = null // 0:未知, 1:男, 2:女
)

// ========== 响应数据类 ==========

data class LoginResponse(
    val user: UserProfile,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class RegisterResponse(
    val user: UserProfile,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class UserProfile(
    val id: String,
    val email: String,
    val username: String,
    val avatar: String? = null,
    val signature: String? = null,
    val birthday: String? = null,
    val gender: Int = 0,
    val isVerified: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)
