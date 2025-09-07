package com.xichen.matelink.core.common.constants

/**
 * 应用常量定义
 */
object AppConstants {

    /**
     * API相关常量
     */
    object Api {
        const val BASE_URL = "https://api.matelink.com/"
        const val CONNECT_TIMEOUT = 30L
        const val READ_TIMEOUT = 30L
        const val WRITE_TIMEOUT = 30L

        // API版本
        const val API_VERSION = "v1"

        // 分页
        const val DEFAULT_PAGE_SIZE = 20
        const val MAX_PAGE_SIZE = 100
    }

    /**
     * 缓存相关常量
     */
    object Cache {
        const val IMAGE_CACHE_SIZE = 100L * 1024L * 1024L // 100MB
        const val HTTP_CACHE_SIZE = 50L * 1024L * 1024L // 50MB
        const val MEMORY_CACHE_SIZE = 50L * 1024L * 1024L // 50MB
    }

    /**
     * 文件相关常量
     */
    object File {
        const val IMAGE_MAX_SIZE = 10L * 1024L * 1024L // 10MB
        const val VIDEO_MAX_SIZE = 100L * 1024L * 1024L // 100MB
    }

    /**
     * 时间相关常量
     */
    object Time {
        const val SPLASH_DELAY = 2000L // 启动页延迟2秒
        const val TOKEN_REFRESH_THRESHOLD = 300L // Token刷新阈值5分钟
        const val CACHE_VALID_TIME = 3600L // 缓存有效时间1小时
    }

    /**
     * 数据库相关常量
     */
    object Database {
        const val NAME = "matelink.db"
        const val VERSION = 1
    }

    /**
     * SharedPreferences相关常量
     */
    object Preferences {
        const val NAME = "matelink_prefs"
        const val THEME_KEY = "theme_mode"
        const val LANGUAGE_KEY = "language"
        const val USER_TOKEN_KEY = "user_token"
        const val USER_REFRESH_TOKEN_KEY = "user_refresh_token"
    }

    /**
     * 通知相关常量
     */
    object Notification {
        const val CHANNEL_ID = "matelink_channel"
        const val CHANNEL_NAME = "MateLink"
        const val CHANNEL_DESCRIPTION = "MateLink消息通知"
    }

    /**
     * 权限请求码
     */
    object Permission {
        const val CAMERA = 1001
        const val STORAGE = 1002
        const val LOCATION = 1003
        const val CONTACTS = 1004
    }
}
