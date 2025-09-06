package com.xichen.matelink.core.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 自定义日志拦截器
 * 提供更详细和可控的网络请求日志
 */
@Singleton
class LoggingInterceptor @Inject constructor() : Interceptor {
    
    private val tag = "NetworkLog"
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        if (!BuildConfig.DEBUG) {
            return chain.proceed(request)
        }
        
        val startTime = System.nanoTime()
        
        // 记录请求信息
        logRequest(request)
        
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Log.e(tag, "HTTP FAILED: $e")
            throw e
        }
        
        val endTime = System.nanoTime()
        val duration = (endTime - startTime) / 1e6
        
        // 记录响应信息
        logResponse(response, duration)
        
        return response
    }
    
    /**
     * 记录请求信息
     */
    private fun logRequest(request: okhttp3.Request) {
        val url = request.url
        val method = request.method
        
        Log.d(tag, "┌────── Request ──────")
        Log.d(tag, "│ $method $url")
        
        // 记录请求头
        val headers = request.headers
        if (headers.size > 0) {
            Log.d(tag, "│ Headers:")
            for (i in 0 until headers.size) {
                val name = headers.name(i)
                val value = headers.value(i)
                Log.d(tag, "│   $name: $value")
            }
        }
        
        // 记录请求体
        val requestBody = request.body
        if (requestBody != null) {
            Log.d(tag, "│ Body:")
            try {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                val charset = requestBody.contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
                val bodyString = buffer.readString(charset)
                Log.d(tag, "│   $bodyString")
            } catch (e: IOException) {
                Log.d(tag, "│   (binary body omitted)")
            }
        }
        
        Log.d(tag, "└─────────────────────")
    }
    
    /**
     * 记录响应信息
     */
    private fun logResponse(response: Response, duration: Double) {
        val request = response.request
        val url = request.url
        val code = response.code
        val message = response.message
        
        Log.d(tag, "┌────── Response ─────")
        Log.d(tag, "│ $code $message $url (${String.format("%.1f", duration)}ms)")
        
        // 记录响应头
        val headers = response.headers
        if (headers.size > 0) {
            Log.d(tag, "│ Headers:")
            for (i in 0 until headers.size) {
                val name = headers.name(i)
                val value = headers.value(i)
                Log.d(tag, "│   $name: $value")
            }
        }
        
        // 记录响应体
        val responseBody = response.body
        if (responseBody != null && response.promisesBody()) {
            try {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE)
                val buffer = source.buffer
                
                val charset: Charset = responseBody.contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
                
                if (responseBody.contentLength() != 0L) {
                    val bodyString = buffer.clone().readString(charset)
                    Log.d(tag, "│ Body:")
                    
                    // 格式化JSON响应（如果是JSON）
                    if (isJsonResponse(response)) {
                        Log.d(tag, "│   ${formatJson(bodyString)}")
                    } else {
                        Log.d(tag, "│   $bodyString")
                    }
                }
            } catch (e: IOException) {
                Log.d(tag, "│   (response body omitted)")
            }
        }
        
        Log.d(tag, "└─────────────────────")
    }
    
    /**
     * 判断是否为JSON响应
     */
    private fun isJsonResponse(response: Response): Boolean {
        val contentType = response.body?.contentType()
        return contentType?.type == "application" && 
               (contentType.subtype == "json" || contentType.subtype.endsWith("+json"))
    }
    
    /**
     * 格式化JSON字符串
     */
    private fun formatJson(json: String): String {
        return try {
            // 简单的JSON格式化，可以使用Gson或其他库进行更好的格式化
            json.replace(",", ",\n│     ")
                .replace("{", "{\n│     ")
                .replace("}", "\n│   }")
                .replace("[", "[\n│     ")
                .replace("]", "\n│   ]")
        } catch (e: Exception) {
            json
        }
    }
}
