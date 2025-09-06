package com.xichen.matelink.core.network.api

import com.xichen.matelink.core.network.auth.ApiResponse
import com.xichen.matelink.core.network.model.PageResponse
import retrofit2.http.*

/**
 * 空间相关API接口
 */
interface SpaceApi {
    
    /**
     * 获取用户空间列表
     */
    @GET("spaces")
    suspend fun getSpaces(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PageResponse<Space>>
    
    /**
     * 创建空间
     */
    @POST("spaces")
    suspend fun createSpace(@Body request: CreateSpaceRequest): ApiResponse<Space>
    
    /**
     * 获取空间详情
     */
    @GET("spaces/{spaceId}")
    suspend fun getSpaceDetail(@Path("spaceId") spaceId: String): ApiResponse<SpaceDetail>
    
    /**
     * 更新空间信息
     */
    @PUT("spaces/{spaceId}")
    suspend fun updateSpace(
        @Path("spaceId") spaceId: String,
        @Body request: UpdateSpaceRequest
    ): ApiResponse<Space>
    
    /**
     * 删除空间
     */
    @DELETE("spaces/{spaceId}")
    suspend fun deleteSpace(@Path("spaceId") spaceId: String): ApiResponse<Unit>
    
    /**
     * 生成加入码
     */
    @POST("spaces/{spaceId}/join-code")
    suspend fun generateJoinCode(@Path("spaceId") spaceId: String): ApiResponse<JoinCodeResponse>
    
    /**
     * 通过加入码申请加入空间
     */
    @POST("spaces/join")
    suspend fun joinSpace(@Body request: JoinSpaceRequest): ApiResponse<Unit>
    
    /**
     * 获取空间成员列表
     */
    @GET("spaces/{spaceId}/members")
    suspend fun getSpaceMembers(
        @Path("spaceId") spaceId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PageResponse<SpaceMember>>
    
    /**
     * 审核加入申请
     */
    @POST("spaces/{spaceId}/approve")
    suspend fun approveJoinRequest(
        @Path("spaceId") spaceId: String,
        @Body request: ApproveJoinRequest
    ): ApiResponse<Unit>
    
    /**
     * 移除成员
     */
    @DELETE("spaces/{spaceId}/members/{userId}")
    suspend fun removeMember(
        @Path("spaceId") spaceId: String,
        @Path("userId") userId: String
    ): ApiResponse<Unit>
    
    /**
     * 设置成员权限
     */
    @PUT("spaces/{spaceId}/members/{userId}/role")
    suspend fun setMemberRole(
        @Path("spaceId") spaceId: String,
        @Path("userId") userId: String,
        @Body request: SetRoleRequest
    ): ApiResponse<Unit>
    
    /**
     * 退出空间
     */
    @POST("spaces/{spaceId}/leave")
    suspend fun leaveSpace(@Path("spaceId") spaceId: String): ApiResponse<Unit>
}

// ========== 数据模型 ==========

data class Space(
    val id: String,
    val name: String,
    val description: String,
    val avatar: String? = null,
    val type: SpaceType,
    val memberCount: Int,
    val maxMembers: Int = 100,
    val isOwner: Boolean,
    val role: MemberRole,
    val joinCode: String? = null,
    val createdAt: String,
    val updatedAt: String
)

data class SpaceDetail(
    val space: Space,
    val members: List<SpaceMember>,
    val recentActivities: List<SpaceActivity>
)

data class SpaceMember(
    val userId: String,
    val username: String,
    val avatar: String? = null,
    val role: MemberRole,
    val joinedAt: String,
    val lastActiveAt: String? = null,
    val isOnline: Boolean = false
)

data class SpaceActivity(
    val id: String,
    val type: ActivityType,
    val description: String,
    val userId: String,
    val username: String,
    val createdAt: String
)

enum class SpaceType {
    PERSONAL,       // 个人空间
    GROUP,          // 群组空间
    PUBLIC          // 公共空间
}

enum class MemberRole {
    OWNER,          // 创建者
    ADMIN,          // 管理员
    MEMBER          // 普通成员
}

enum class ActivityType {
    CREATE_SPACE,   // 创建空间
    JOIN_SPACE,     // 加入空间
    LEAVE_SPACE,    // 离开空间
    SEND_MESSAGE,   // 发送消息
    POST_MOMENT     // 发布动态
}

// ========== 请求数据类 ==========

data class CreateSpaceRequest(
    val name: String,
    val description: String,
    val type: SpaceType = SpaceType.GROUP,
    val avatar: String? = null,
    val maxMembers: Int = 100
)

data class UpdateSpaceRequest(
    val name: String? = null,
    val description: String? = null,
    val avatar: String? = null,
    val maxMembers: Int? = null
)

data class JoinSpaceRequest(
    val joinCode: String,
    val message: String? = null
)

data class ApproveJoinRequest(
    val userId: String,
    val approve: Boolean,
    val message: String? = null
)

data class SetRoleRequest(
    val role: MemberRole
)

// ========== 响应数据类 ==========

data class JoinCodeResponse(
    val joinCode: String,
    val expiresAt: String
)
