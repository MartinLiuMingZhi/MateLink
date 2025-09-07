package com.xichen.matelink.core.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 图片相关依赖注入模块
 * 注意：图片相关的依赖注入在 core:ui 模块中定义
 */

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {
    // 图片相关的依赖注入在 core:ui 模块的 ImageLoaderModule 中定义
}
