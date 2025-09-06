package com.xichen.matelink.core.network.utils

import com.xichen.matelink.core.network.error.NetworkError
import com.xichen.matelink.core.network.error.NetworkErrorHandler
import com.xichen.matelink.core.network.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 网络工具类
 * 提供网络请求的便捷方法
 */
@Singleton
class NetworkUtils @Inject constructor(
    private val errorHandler: NetworkErrorHandler
) {
    
    /**
     * 安全的网络请求执行
     */
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(
                        exception = Exception("Response body is null"),
                        message = "服务器返回数据为空"
                    )
                }
            } else {
                val error = errorHandler.handleError(
                    retrofit2.HttpException(response)
                )
                Result.Error(
                    exception = error.throwable,
                    message = error.message
                )
            }
        } catch (throwable: Throwable) {
            val error = errorHandler.handleError(throwable)
            Result.Error(
                exception = error.throwable,
                message = error.message
            )
        }
    }
    
    /**
     * 带重试的网络请求
     */
    suspend fun <T> safeApiCallWithRetry(
        maxRetries: Int = 3,
        apiCall: suspend () -> Response<T>
    ): Result<T> {
        var lastError: NetworkError? = null
        
        repeat(maxRetries) { attempt ->
            when (val result = safeApiCall(apiCall)) {
                is Result.Success -> return result
                is Result.Error -> {
                    lastError = NetworkError(
                        type = com.xichen.matelink.core.network.error.ErrorType.UNKNOWN,
                        message = result.message,
                        throwable = result.exception
                    )
                    
                    // 如果不是可重试的错误，直接返回
                    if (!errorHandler.isRetryable(lastError!!)) {
                        return result
                    }
                    
                    // 最后一次重试失败，返回错误
                    if (attempt == maxRetries - 1) {
                        return result
                    }
                    
                    // 等待一段时间后重试
                    kotlinx.coroutines.delay(1000L * (attempt + 1))
                }
                is Result.Loading -> {
                    // 不应该出现这种情况
                }
            }
        }
        
        return Result.Error(
            exception = lastError?.throwable ?: Exception("Unknown error"),
            message = lastError?.message ?: "请求失败"
        )
    }
    
    /**
     * 处理分页请求
     */
    suspend fun <T> handlePagedRequest(
        apiCall: suspend () -> Response<com.xichen.matelink.core.network.auth.ApiResponse<com.xichen.matelink.core.network.model.PageResponse<T>>>
    ): Result<com.xichen.matelink.core.network.model.PageResponse<T>> {
        return safeApiCall {
            val response = apiCall()
            if (response.isSuccessful && response.body()?.success == true) {
                Response.success(response.body()?.data)
            } else {
                response
            }
        }
    }
    
    /**
     * 格式化文件大小
     */
    fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        
        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$bytes B"
        }
    }
    
    /**
     * 检查URL是否有效
     */
    fun isValidUrl(url: String): Boolean {
        return try {
            val pattern = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$".toRegex()
            pattern.matches(url)
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取文件扩展名
     */
    fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
    }
    
    /**
     * 根据文件扩展名获取MIME类型
     */
    fun getMimeType(fileName: String): String {
        val extension = getFileExtension(fileName).lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "mp4" -> "video/mp4"
            "avi" -> "video/avi"
            "mov" -> "video/mov"
            "mp3" -> "audio/mp3"
            "wav" -> "audio/wav"
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "txt" -> "text/plain"
            else -> "application/octet-stream"
        }
    }
}

/**
 * 网络请求状态
 */
data class RequestStatus(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

/**
 * 网络请求扩展函数
 */

/**
 * Response扩展：转换为Result
 */
fun <T> Response<T>.toResult(): Result<T> {
    return if (isSuccessful) {
        val body = body()
        if (body != null) {
            Result.Success(body)
        } else {
            Result.Error(
                exception = Exception("Response body is null"),
                message = "服务器返回数据为空"
            )
        }
    } else {
        Result.Error(
            exception = retrofit2.HttpException(this),
            message = "请求失败: ${code()}"
        )
    }
}
