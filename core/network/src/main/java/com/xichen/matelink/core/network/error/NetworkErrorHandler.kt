package com.xichen.matelink.core.network.error

import com.google.gson.JsonSyntaxException
import com.xichen.matelink.core.network.interceptor.NoNetworkException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 网络错误处理器
 * 统一处理各种网络异常
 */
@Singleton
class NetworkErrorHandler @Inject constructor() {
    
    /**
     * 处理异常并返回用户友好的错误信息
     */
    fun handleError(throwable: Throwable): NetworkError {
        return when (throwable) {
            // 网络连接异常
            is NoNetworkException -> NetworkError(
                type = ErrorType.NO_NETWORK,
                message = "网络连接不可用，请检查网络设置",
                throwable = throwable
            )
            
            is UnknownHostException -> NetworkError(
                type = ErrorType.NO_NETWORK,
                message = "无法连接到服务器，请检查网络连接",
                throwable = throwable
            )
            
            is SocketTimeoutException -> NetworkError(
                type = ErrorType.TIMEOUT,
                message = "网络连接超时，请稍后重试",
                throwable = throwable
            )
            
            is IOException -> NetworkError(
                type = ErrorType.NETWORK_ERROR,
                message = "网络请求失败，请检查网络连接",
                throwable = throwable
            )
            
            // HTTP异常
            is HttpException -> handleHttpException(throwable)
            
            // JSON解析异常
            is JsonSyntaxException -> NetworkError(
                type = ErrorType.PARSE_ERROR,
                message = "数据解析失败，请稍后重试",
                throwable = throwable
            )
            
            // 自定义业务异常
            is BusinessException -> NetworkError(
                type = ErrorType.BUSINESS_ERROR,
                message = throwable.message ?: "业务处理失败",
                code = throwable.code,
                throwable = throwable
            )
            
            // 未知异常
            else -> NetworkError(
                type = ErrorType.UNKNOWN,
                message = throwable.message ?: "未知错误，请稍后重试",
                throwable = throwable
            )
        }
    }
    
    /**
     * 处理HTTP异常
     */
    private fun handleHttpException(exception: HttpException): NetworkError {
        val code = exception.code()
        val message = when (code) {
            400 -> "请求参数错误"
            401 -> "登录已过期，请重新登录"
            403 -> "权限不足，无法访问"
            404 -> "请求的资源不存在"
            405 -> "请求方法不支持"
            408 -> "请求超时，请稍后重试"
            409 -> "请求冲突，请稍后重试"
            429 -> "请求过于频繁，请稍后重试"
            500 -> "服务器内部错误，请稍后重试"
            502 -> "网关错误，请稍后重试"
            503 -> "服务暂时不可用，请稍后重试"
            504 -> "网关超时，请稍后重试"
            else -> "网络请求失败 (${code})"
        }
        
        val errorType = when (code) {
            401 -> ErrorType.UNAUTHORIZED
            403 -> ErrorType.FORBIDDEN
            404 -> ErrorType.NOT_FOUND
            408, 504 -> ErrorType.TIMEOUT
            429 -> ErrorType.RATE_LIMIT
            in 500..599 -> ErrorType.SERVER_ERROR
            else -> ErrorType.HTTP_ERROR
        }
        
        return NetworkError(
            type = errorType,
            message = message,
            code = code,
            throwable = exception
        )
    }
    
    /**
     * 获取错误的用户提示
     */
    fun getErrorMessage(error: NetworkError): String {
        return when (error.type) {
            ErrorType.NO_NETWORK -> "网络不可用，请检查网络设置"
            ErrorType.TIMEOUT -> "连接超时，请稍后重试"
            ErrorType.UNAUTHORIZED -> "登录已过期，请重新登录"
            ErrorType.FORBIDDEN -> "权限不足"
            ErrorType.NOT_FOUND -> "请求的内容不存在"
            ErrorType.RATE_LIMIT -> "操作过于频繁，请稍后再试"
            ErrorType.SERVER_ERROR -> "服务器繁忙，请稍后重试"
            ErrorType.PARSE_ERROR -> "数据解析错误"
            ErrorType.BUSINESS_ERROR -> error.message
            else -> "网络异常，请稍后重试"
        }
    }
    
    /**
     * 判断错误是否可以重试
     */
    fun isRetryable(error: NetworkError): Boolean {
        return when (error.type) {
            ErrorType.TIMEOUT,
            ErrorType.NETWORK_ERROR,
            ErrorType.SERVER_ERROR -> true
            else -> false
        }
    }
}

/**
 * 网络错误数据类
 */
data class NetworkError(
    val type: ErrorType,
    val message: String,
    val code: Int = -1,
    val throwable: Throwable
)

/**
 * 错误类型枚举
 */
enum class ErrorType {
    NO_NETWORK,         // 无网络
    TIMEOUT,            // 超时
    NETWORK_ERROR,      // 网络错误
    HTTP_ERROR,         // HTTP错误
    UNAUTHORIZED,       // 未授权
    FORBIDDEN,          // 禁止访问
    NOT_FOUND,          // 资源不存在
    RATE_LIMIT,         // 请求频率限制
    SERVER_ERROR,       // 服务器错误
    PARSE_ERROR,        // 解析错误
    BUSINESS_ERROR,     // 业务错误
    UNKNOWN             // 未知错误
}

/**
 * 业务异常类
 */
class BusinessException(
    message: String,
    val code: Int = -1
) : Exception(message)
