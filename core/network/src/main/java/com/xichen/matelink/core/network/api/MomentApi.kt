package com.xichen.matelink.core.network.api

import com.xichen.matelink.core.network.auth.ApiResponse
import com.xichen.matelink.core.network.model.PageResponse
import retrofit2.http.*

/**
 * 朋友圈相关API接口
 */
interface MomentApi {
    
    /**
     * 获取朋友圈动态列表
     */
    @GET("moments")
    suspend fun getMoments(
        @Query("spaceId") spaceId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("lastMomentId") lastMomentId: String? = null
    ): ApiResponse<PageResponse<Moment>>
    
    /**
     * 发布动态
     */
    @POST("moments")
    suspend fun publishMoment(@Body request: PublishMomentRequest): ApiResponse<Moment>
    
    /**
     * 删除动态
     */
    @DELETE("moments/{momentId}")
    suspend fun deleteMoment(@Path("momentId") momentId: String): ApiResponse<Unit>
    
    /**
     * 点赞/取消点赞
     */
    @POST("moments/{momentId}/like")
    suspend fun toggleLike(@Path("momentId") momentId: String): ApiResponse<LikeResponse>
    
    /**
     * 获取点赞列表
     */
    @GET("moments/{momentId}/likes")
    suspend fun getLikes(
        @Path("momentId") momentId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PageResponse<Like>>
    
    /**
     * 发表评论
     */
    @POST("moments/{momentId}/comments")
    suspend fun addComment(
        @Path("momentId") momentId: String,
        @Body request: AddCommentRequest
    ): ApiResponse<Comment>
    
    /**
     * 获取评论列表
     */
    @GET("moments/{momentId}/comments")
    suspend fun getComments(
        @Path("momentId") momentId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PageResponse<Comment>>
    
    /**
     * 删除评论
     */
    @DELETE("moments/{momentId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("momentId") momentId: String,
        @Path("commentId") commentId: String
    ): ApiResponse<Unit>
    
    /**
     * 回复评论
     */
    @POST("moments/{momentId}/comments/{commentId}/reply")
    suspend fun replyComment(
        @Path("momentId") momentId: String,
        @Path("commentId") commentId: String,
        @Body request: ReplyCommentRequest
    ): ApiResponse<Comment>
    
    /**
     * 举报动态
     */
    @POST("moments/{momentId}/report")
    suspend fun reportMoment(
        @Path("momentId") momentId: String,
        @Body request: ReportRequest
    ): ApiResponse<Unit>
}

// ========== 数据模型 ==========

data class Moment(
    val id: String,
    val content: String,
    val images: List<String> = emptyList(),
    val video: String? = null,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String? = null,
    val spaceId: String,
    val spaceName: String,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val isLiked: Boolean = false,
    val privacy: MomentPrivacy = MomentPrivacy.SPACE_FRIENDS,
    val location: String? = null,
    val tags: List<String> = emptyList(),
    val createdAt: String,
    val updatedAt: String
)

data class Like(
    val id: String,
    val userId: String,
    val username: String,
    val avatar: String? = null,
    val createdAt: String
)

data class Comment(
    val id: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String? = null,
    val replyToId: String? = null,
    val replyToUser: String? = null,
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val replies: List<Comment> = emptyList(),
    val createdAt: String,
    val updatedAt: String
)

enum class MomentPrivacy {
    PUBLIC,             // 公开
    SPACE_FRIENDS,      // 空间好友可见
    PRIVATE             // 仅自己可见
}

enum class ReportType {
    SPAM,               // 垃圾信息
    INAPPROPRIATE,      // 不当内容
    HARASSMENT,         // 骚扰
    COPYRIGHT,          // 版权问题
    OTHER               // 其他
}

// ========== 请求数据类 ==========

data class PublishMomentRequest(
    val content: String,
    val images: List<String> = emptyList(),
    val video: String? = null,
    val spaceId: String,
    val privacy: MomentPrivacy = MomentPrivacy.SPACE_FRIENDS,
    val location: String? = null,
    val tags: List<String> = emptyList()
)

data class AddCommentRequest(
    val content: String,
    val replyToId: String? = null
)

data class ReplyCommentRequest(
    val content: String
)

data class ReportRequest(
    val type: ReportType,
    val reason: String
)

// ========== 响应数据类 ==========

data class LikeResponse(
    val isLiked: Boolean,
    val likeCount: Int
)
