package com.xichen.matelink.core.network.api

import com.xichen.matelink.core.network.auth.ApiResponse
import com.xichen.matelink.core.network.model.PageResponse
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * 聊天相关API接口
 */
interface ChatApi {
    
    /**
     * 发送消息
     */
    @POST("chat/messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): ApiResponse<Message>
    
    /**
     * 获取消息列表
     */
    @GET("chat/messages")
    suspend fun getMessages(
        @Query("spaceId") spaceId: String,
        @Query("receiverId") receiverId: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("lastMessageId") lastMessageId: String? = null
    ): ApiResponse<PageResponse<Message>>
    
    /**
     * 撤回消息
     */
    @DELETE("chat/messages/{messageId}")
    suspend fun recallMessage(@Path("messageId") messageId: String): ApiResponse<Unit>
    
    /**
     * 标记消息已读
     */
    @PUT("chat/messages/{messageId}/read")
    suspend fun markMessageAsRead(@Path("messageId") messageId: String): ApiResponse<Unit>
    
    /**
     * 批量标记消息已读
     */
    @PUT("chat/messages/batch-read")
    suspend fun batchMarkAsRead(@Body request: BatchReadRequest): ApiResponse<Unit>
    
    /**
     * 上传文件
     */
    @Multipart
    @POST("chat/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("type") type: String
    ): ApiResponse<FileUploadResponse>
    
    /**
     * 获取会话列表
     */
    @GET("chat/conversations")
    suspend fun getConversations(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PageResponse<Conversation>>
    
    /**
     * 删除会话
     */
    @DELETE("chat/conversations/{conversationId}")
    suspend fun deleteConversation(@Path("conversationId") conversationId: String): ApiResponse<Unit>
    
    /**
     * 置顶会话
     */
    @PUT("chat/conversations/{conversationId}/pin")
    suspend fun pinConversation(
        @Path("conversationId") conversationId: String,
        @Body request: PinConversationRequest
    ): ApiResponse<Unit>
    
    /**
     * 搜索消息
     */
    @GET("chat/search")
    suspend fun searchMessages(
        @Query("keyword") keyword: String,
        @Query("spaceId") spaceId: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PageResponse<Message>>
}

// ========== 数据模型 ==========

data class Message(
    val id: String,
    val content: String,
    val type: MessageType,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String? = null,
    val receiverId: String? = null,
    val spaceId: String,
    val conversationId: String,
    val replyToId: String? = null,
    val replyToMessage: Message? = null,
    val status: MessageStatus,
    val readBy: List<String> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
    val fileUrl: String? = null,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val duration: Int? = null, // 语音/视频时长（秒）
    val thumbnailUrl: String? = null
)

enum class MessageType {
    TEXT,           // 文本消息
    IMAGE,          // 图片消息
    VOICE,          // 语音消息
    VIDEO,          // 视频消息
    FILE,           // 文件消息
    EMOJI,          // 表情消息
    SYSTEM          // 系统消息
}

enum class MessageStatus {
    SENDING,        // 发送中
    SENT,           // 已发送
    DELIVERED,      // 已送达
    READ,           // 已读
    FAILED          // 发送失败
}

data class Conversation(
    val id: String,
    val type: ConversationType,
    val spaceId: String? = null,
    val participantId: String? = null,
    val participantName: String? = null,
    val participantAvatar: String? = null,
    val spaceName: String? = null,
    val spaceAvatar: String? = null,
    val lastMessage: Message? = null,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val updatedAt: String
)

enum class ConversationType {
    PRIVATE,        // 私聊
    GROUP,          // 群聊
    SYSTEM          // 系统消息
}

// ========== 请求数据类 ==========

data class SendMessageRequest(
    val content: String,
    val type: MessageType,
    val spaceId: String,
    val receiverId: String? = null,
    val replyToId: String? = null,
    val fileUrl: String? = null,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val duration: Int? = null
)

data class BatchReadRequest(
    val messageIds: List<String>
)

data class PinConversationRequest(
    val pinned: Boolean
)

// ========== 响应数据类 ==========

data class FileUploadResponse(
    val url: String,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val thumbnailUrl: String? = null
)
