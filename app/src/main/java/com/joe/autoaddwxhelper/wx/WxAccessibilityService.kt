package com.joe.autoaddwxhelper.wx

import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.joe.autoaddwxhelper.showToast

/**
 * Description：
 *
 * @author QiaoJF on 2020/4/27.
 */
class WxAccessibilityService : BaseAccessibilityService() {

    private val TAG = WxAccessibilityService::class.java.simpleName


    companion object {
        /**
         * 添加动作是否完成标志.
         * 避免添加成功后重复操作
         */
        var isAddDone = false
        /**
         * 是否是添加好友操作标志
         * 用于及时中断,并返回app
         */
        var isAddAction = false
        /**
         * 当前在微信添加的步骤.
         * 用于在TYPE_WINDOW_CONTENT_CHANGED时区分
         */
        var addStep = 0
        var wxId = ""
        /**
         * 添加好友的问候语
         */
        var addHint = ""
        fun setAddInfo(inputWxId: String, inputAddHint: String?) {
            isAddDone = false
            isAddAction = true
            addStep = 0
            wxId = inputWxId
            addHint = inputAddHint ?: ""
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        return super.onKeyEvent(event)
    }

    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {
        val eventType = accessibilityEvent.eventType
        var eventTypeName = ""
        when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                eventTypeName = "TYPE_WINDOW_STATE_CHANGED"
                when (accessibilityEvent.className.toString()) {
                    //微信Home页
                    WxPageClassDef.LAUNCHER -> {
                        checkAddDone(accessibilityEvent, addBlock = {
                            delay(500)
                            //微信的聊天/H5..都是LAUNCHER页.所以根据有无返回图片区分
                            if (findViewByID(WxViewIdDef.IV_BACK) != null) {
                                performBackClick()
                            } else {
                                clickTextViewByID(WxViewIdDef.IV_SEARCH)
                                addStep = 1
                            }
                        })
                    }
                    //添加朋友页 (未使用.改为搜索按钮触发)
                    WxPageClassDef.ADD_FRIEND -> {
                        val inputViewInfo = findViewByID("com.tencent.mm:id/bfl")
                        delay(50)
                        inputText(inputViewInfo, wxId)
                    }
                    //添加朋友->搜索页 (未使用.改为搜索按钮触发)
                    WxPageClassDef.ADD_FRIEND_MORE -> {
//                        clickTextViewByID("com.tencent.mm:id/f54")
                        delay(200)
//                        clickTextViewByID("com.tencent.mm:id/f9j")
//                        clickTextViewByID("com.tencent.mm:id/f54")
//                        setSelectedListItem("android:id/list", 1)
                        clickTextViewByText("微信号")
                    }
                    //搜索按钮-搜索页
                    WxPageClassDef.SEARCH -> {
                        checkAddDone(accessibilityEvent, addBlock = {
                            //延迟用于隐藏输入法
                            delay(500)
                            performBackClick()
                            val inputViewInfo = findViewByID(WxViewIdDef.EDT_SEARCH)
                            delay(500)
                            addStep = 2
                            inputText(inputViewInfo, wxId)
                        })
                    }
                    //搜索到未添加的联系人详情页
                    WxPageClassDef.CONTACT -> {
                        checkAddDone(accessibilityEvent, {
                            //根据有无添加到通讯录View.判断是否是已添加的好友
                            if (findViewByText("添加到通讯录", false) != null) {
                                addStep = 4
                                delay(50)
                                clickTextViewByText("添加到通讯录")

//                                setAddDone()
                            } else {
                                //已添加的好友
                                setAddDone()
                                //无触发change的操作.收到触发back
                                performBackClick()
                            }
                        })
                    }
                    //申请添加朋友页
                    WxPageClassDef.SAY_HI2ADD -> {
                        //添加朋友申请Edt
                        if (addHint.isNullOrEmpty()) {
                            clickTextViewByID(WxViewIdDef.BTN_SEND)
                        } else {
                            val inputViewInfo = findViewByID(WxViewIdDef.EDT_ADD_HINT)
                            delay(50)
                            inputText(inputViewInfo, addHint)
                            clickTextViewByID(WxViewIdDef.BTN_SEND)
                        }
                        delay(500)
                        showToast("添加完成,返回中...")
                        setAddDone()
                    }
                    else -> {
                        //进入微信时发现并不处于LAUNCHER页,所以先返回至LAUNCHER
                        checkAddDone(accessibilityEvent, addBlock = {
                            performBackClick()
                        })
                    }
                }

            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                eventTypeName = "TYPE_WINDOW_CONTENT_CHANGED"
                if (!isAddDone && isAddAction && addStep < 3 ) {
                    if (findViewByID(WxViewIdDef.BTN_SEARCH) != null) {
                        checkAddDone (accessibilityEvent, addBlock = {
                            addStep = 3
                            delay(500)
                            clickTextViewByID(WxViewIdDef.BTN_SEARCH)
                        })
                    }
                }
                //用户不存在场景判断
                if (!isAddDone && isAddAction && addStep == 3) {
                    delay(500)
                    if (findViewByText("用户不存在") != null) {
                        setAddDone()
                    } else if (findViewByText("操作过于频繁") != null) {
                        showToast("警告!您当前操作过于频繁,请稍后尝试!")
                        setAddDone()
                    }
                }

            }
        }
        Log.i(TAG, "addStep: " + addStep + " eventTypeName:" + eventTypeName + " " + accessibilityEvent.className)
    }

    /**
     * 设置已完成自动添加动作
     */
    private fun setAddDone() {
        addStep = 5
        isAddDone = true
    }

    /**
     * 校验是否已经完成添加流程
     */
    private fun checkAddDone(accessibilityEvent: AccessibilityEvent, addBlock: () -> Unit) {
        val className = accessibilityEvent.className
        if (!className.isNullOrEmpty()) {
            if (!isAddDone && isAddAction && !className.equals(WxPageClassDef.LOADING)) {
                addBlock()
            } else {
                if (isAddDone) {
                    if (isAddAction) {
                        if (className.equals(WxPageClassDef.LAUNCHER)) {
                            //中断自动添加
                            isAddAction = false
                            isAddDone = false
                        }
                        performBackClick()
                    }
                }
            }
        }

    }

}