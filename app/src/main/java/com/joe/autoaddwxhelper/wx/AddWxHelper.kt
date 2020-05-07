package com.joe.autoaddwxhelper.wx

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.joe.autoaddwxhelper.R
import com.joe.autoaddwxhelper.wx.BaseAccessibilityService.Companion.instance
import com.joe.autoaddwxhelper.wx.WxAccessibilityService.Companion.setAddInfo
import java.lang.ref.WeakReference

/**
 * Description：
 *     监听生命周期.实现添加微信好友帮助类
 * @author QiaoJF on 2020/5/6.
 */
class AddWxHelper(context: Context) : ILifecycle {

    private var mWeakRfContext: WeakReference<Context> = WeakReference(context)
    /**
     *
     * 是否申请了无障碍服务权限状态值
     * 用于在[onResume]中重新校验权限
     */
    private var mIsRequestedAccessibilityPms = false
    private var mCurrentNeedAddWxId: String? = null


    override fun onCreate() {}

    override fun onPause() {}

    override fun onResume() {
        if (mIsRequestedAccessibilityPms) {
            val context = mWeakRfContext.get() ?: return
            instance!!.init(context)
            if (!instance!!.checkAccessibilityEnabled(WxAccessibilityService::class.java.simpleName)) {
                showToast("请您先开启" + context.getString(R.string.txt_wx_accessibility_name))
                instance!!.goAccess()
            } else {
                mIsRequestedAccessibilityPms = false
                turn2addWx()
            }
        }
    }

    override fun onDestroy() {
        mWeakRfContext.clear()
    }

    fun check2add(wxId: String?) {
        val context = mWeakRfContext.get() ?: return
        mCurrentNeedAddWxId = wxId
        if (!wxId.isNullOrEmpty()) {
            instance!!.init(context)
            if (!instance!!.checkAccessibilityEnabled(WxAccessibilityService::class.java.simpleName)) {
                AlertDialog.Builder(context)
                    .setMessage(
                        String.format(
                            context.getString(R.string.txt_turn2setting_wx_accessibility),
                            context.getString(R.string.txt_wx_accessibility_name)
                        )
                    )
                    .setPositiveButton("确定") { p0, p1 ->
                        mIsRequestedAccessibilityPms = true
                        instance!!.goAccess()
                    }
                    .show()
            } else {
                turn2addWx()
            }
        } else {
            showToast("该线索没有微信")
        }
    }

    private fun turn2addWx() {
        val userName = "小小微助手"
        setAddInfo(mCurrentNeedAddWxId!!, String.format(mWeakRfContext.get()!!.getString(R.string.txt_add_wx_hint), userName))
        openWechat()
    }

    /**
     * 跳转到微信
     */
    private fun openWechat() {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            val cmp = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.component = cmp
            mWeakRfContext.get()?.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToast("检查到您手机没有安装微信，请安装后使用该功能！")
        }
    }

    private fun showToast(hint: String) {
        Toast.makeText(mWeakRfContext.get(), hint, Toast.LENGTH_SHORT).show()
    }

}