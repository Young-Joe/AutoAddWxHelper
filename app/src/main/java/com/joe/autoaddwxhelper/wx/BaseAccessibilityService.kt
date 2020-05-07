package com.joe.autoaddwxhelper.wx

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi

/**
 * Description：
 *
 * @author QiaoJF on 2020/4/27.
 */
open class BaseAccessibilityService : AccessibilityService() {
    
    private var mAccessibilityManager: AccessibilityManager? = null
    private var mContext: Context? = null

    fun init(context: Context) {
        mContext = context.applicationContext
        mAccessibilityManager = mContext?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    }

    /**
     * Check当前辅助服务是否启用
     *
     * @param serviceName serviceName
     * @return 是否启用
     */
    fun checkAccessibilityEnabled(serviceName: String): Boolean {
        val accessibilityServices = mAccessibilityManager!!.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        for (info in accessibilityServices) {
            if (info.id.contains(serviceName)) {
                return true
            }
        }
        return false
    }

    /**
     * 前往开启辅助服务界面
     */
    fun goAccess() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext!!.startActivity(intent)
    }

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    fun performViewClick(nodeInfo: AccessibilityNodeInfo?) {
        var nodeInfo: AccessibilityNodeInfo? = nodeInfo ?: return
        while (nodeInfo != null) {
            if (nodeInfo.isClickable) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                break
            }
            nodeInfo = nodeInfo.parent
        }
    }

    /**
     * 模拟返回操作
     */
    fun performBackClick() {
        delay(200)
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    /**
     * 模拟下滑操作
     */
    fun performScrollBackward() {
        delay(500)
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
    }

    /**
     * 模拟上滑操作
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun performScrollForward() {
        delay(500)
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
    }
    /**
     * 查找对应文本的View
     *
     * @param text      text
     * @param clickable 该View是否可以点击
     * @return View
     */
    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    @JvmOverloads
    fun findViewByText(text: String?, clickable: Boolean = false): AccessibilityNodeInfo? {
        val accessibilityNodeInfo = rootInActiveWindow ?: return null
        val nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (nodeInfo in nodeInfoList) {
                if (nodeInfo != null && nodeInfo.isClickable == clickable) {
                    return nodeInfo
                }
            }
        }
        return null
    }

    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun findViewByID(id: String?): AccessibilityNodeInfo? {
        val accessibilityNodeInfo = rootInActiveWindow ?: return null
        val nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id)
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            Log.d("dd", "findViewByID: " + nodeInfoList.size)
            for (nodeInfo in nodeInfoList) {
                Log.d("dd", "findViewByID: $nodeInfo")
                if (nodeInfo != null) {
                    return nodeInfo
                }
            }
        }
        return null
    }

    fun clickTextViewByText(text: String?) {
        val accessibilityNodeInfo = rootInActiveWindow ?: return
        val nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)
        if (nodeInfoList != null && nodeInfoList.isNotEmpty()) {
            for (nodeInfo in nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo)
                    break
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun clickTextViewByID(id: String?) {
        val accessibilityNodeInfo = rootInActiveWindow ?: return
        val nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id)
        if (nodeInfoList != null && nodeInfoList.isNotEmpty()) {
            for (nodeInfo in nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo)
                    break
                }
            }
        }
    }

    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    fun inputText(nodeInfo: AccessibilityNodeInfo?, text: String?) {
        if (nodeInfo == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val arguments = Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        }
    }


    /**
     * 设置ListView列表逐行往下滚动（GridView也类似）
     *
     * @param viewId
     */
    fun setListScrollDown(viewId: String) {
        val root = rootInActiveWindow ?: return
        val infoList = root.findAccessibilityNodeInfosByViewId(viewId)
        if (infoList != null && infoList.size > 0) {
            val nodeInfo = infoList[0]
            if (nodeInfo != null && getNodeClass(nodeInfo).contains("ListView")) {
                for (k in 0 until nodeInfo.childCount) {
                    val child = nodeInfo.getChild(k)
                    if (child != null) { //逐行滚动。
                        child.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS)
                        child.performAction(AccessibilityNodeInfo.ACTION_SELECT)
                        // getItemInfoClick(child.getViewIdResourceName());
                    }
                }
            }
        }
    }

    /**
     * 设置选中列表指定item并触发点击事件，（GridView也类似）
     * infoList的大小为当前可见item数量，position的值为当前列表item的位置
     * @param viewId
     * @param position
     */
    fun setSelectedListItem(viewId: String, position: Int) {
        val root = rootInActiveWindow ?: return
        val infoList = root.findAccessibilityNodeInfosByViewId(viewId)
        if (infoList != null && infoList.size > 0) {
            val nodeInfo = infoList[0]
            if (nodeInfo != null && getNodeClass(nodeInfo).contains("ListView")) {
                val childCount = nodeInfo.childCount
                if (position >= 0 && position <= childCount - 1) {
                    val child = nodeInfo.getChild(position)
                    if (child != null) {
                        child.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS)
                        child.performAction(AccessibilityNodeInfo.ACTION_SELECT)
                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                } else {
                    val child = nodeInfo.getChild(0)
                    child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
            }
        }
    }

    /**
     * 设置ListView滚动到顶部（GridView也类似）
     *
     * @param viewId
     */
    fun setListScrollTop(viewId: String) {
        val root = rootInActiveWindow ?: return
        val infoList = root.findAccessibilityNodeInfosByViewId(viewId)
        if (infoList != null && infoList.size > 0) {
            val nodeInfo = infoList[0]
            if (nodeInfo != null && getNodeClass(nodeInfo).contains("ListView")) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
            }
        }
    }

    fun delay(delayTime: Long) {
        try {
            Thread.sleep(delayTime)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun getNodeClass(nodeInfo: AccessibilityNodeInfo): String {
        return nodeInfo.className.toString()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {}
    override fun onInterrupt() {}

    companion object {
        private var mInstance: BaseAccessibilityService? = null
        val instance: BaseAccessibilityService?
            get() {
                if (mInstance == null) {
                    mInstance = BaseAccessibilityService()
                }
                return mInstance
            }
    }
}