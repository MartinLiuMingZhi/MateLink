package com.xichen.matelink.core.network.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * 网络请求结果封装类
 * 统一处理成功、失败、加载状态
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "Unknown error"
    ) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * 网络响应基类
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
    val success: Boolean = code == 200
)

/**
 * 分页响应封装
 */
data class PageResponse<T>(
    val list: List<T>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val hasMore: Boolean = (page * pageSize) < total
)

/**
 * 结果扩展函数
 */

/**
 * 映射结果数据
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(exception, message)
        is Result.Loading -> Result.Loading
    }
}

/**
 * 获取数据或默认值
 */
fun <T> Result<T>.getOrNull(): T? {
    return when (this) {
        is Result.Success -> data
        else -> null
    }
}

/**
 * 获取数据或默认值
 */
fun <T> Result<T>.getOrDefault(defaultValue: T): T {
    return when (this) {
        is Result.Success -> data
        else -> defaultValue
    }
}

/**
 * 是否成功
 */
fun <T> Result<T>.isSuccess(): Boolean {
    return this is Result.Success
}

/**
 * 是否失败
 */
fun <T> Result<T>.isError(): Boolean {
    return this is Result.Error
}

/**
 * 是否加载中
 */
fun <T> Result<T>.isLoading(): Boolean {
    return this is Result.Loading
}

/**
 * 执行成功时的操作
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

/**
 * 执行失败时的操作
 */
inline fun <T> Result<T>.onError(action: (Throwable, String) -> Unit): Result<T> {
    if (this is Result.Error) {
        action(exception, message)
    }
    return this
}

/**
 * 执行加载时的操作
 */
inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) {
        action()
    }
    return this
}

/**
 * Flow扩展：包装为Result
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }
}

/**
 * 安全的API调用
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        Result.Success(apiCall())
    } catch (throwable: Throwable) {
        Result.Error(throwable)
    }
}
