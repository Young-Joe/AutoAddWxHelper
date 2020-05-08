package com.joe.autoaddwxhelper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joe.autoaddwxhelper.wx.AddWxHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mAddWxHelper: AddWxHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnAdd.setOnClickListener {
            val wxId = mEdtInput.text.toString()
            if (wxId.isNullOrEmpty()) {
                showToast("请输入需要添加的微信号")
                return@setOnClickListener
            }
            if (mAddWxHelper == null) {
                mAddWxHelper = AddWxHelper(this)
                lifecycle.addObserver(mAddWxHelper!!)
            }
            mAddWxHelper!!.check2add(wxId)
        }

    }

}
