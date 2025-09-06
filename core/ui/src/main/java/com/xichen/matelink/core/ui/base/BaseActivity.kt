package com.xichen.matelink.core.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.xichen.matelink.core.common.utils.DensityAdapterManager

/**
 * Activity基类
 * 统一处理屏幕适配和通用逻辑
 */
abstract class BaseActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置屏幕适配
        if (isEnableScreenAdapter()) {
            DensityAdapterManager.setDensity(this)
        }
        
        // 设置Compose内容
        setContent {
            Content()
        }
    }
    
    /**
     * 是否启用屏幕适配
     * 子类可重写此方法来控制是否启用适配
     */
    protected open fun isEnableScreenAdapter(): Boolean = true
    
    /**
     * Compose内容
     * 子类实现具体的UI内容
     */
    @Composable
    protected abstract fun Content()
}
