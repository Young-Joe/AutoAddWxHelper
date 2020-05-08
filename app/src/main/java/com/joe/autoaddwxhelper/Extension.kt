package com.joe.autoaddwxhelper

import android.content.Context
import android.widget.Toast

/**
 * Description：
 *
 * @author QiaoJF on 2020/5/8.
 */

fun Context.showToast(hint: String) {
    Toast.makeText(this, hint, Toast.LENGTH_SHORT).show()
}