package com.joe.autoaddwxhelper.wx;


import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description：
 *
 * @author QiaoJF on 2020/4/28.
 */
@StringDef({ WxViewIdDef.IV_SEARCH, WxViewIdDef.EDT_SEARCH, WxViewIdDef.BTN_VIDEO, WxViewIdDef.BTN_SEARCH, WxViewIdDef.BTN_SEND, WxViewIdDef.EDT_ADD_HINT, WxViewIdDef.IV_BACK })
@Retention(RetentionPolicy.SOURCE)
public @interface WxViewIdDef {

    /**
     * 微信home页.搜索图标id
     */
    String IV_SEARCH = "com.tencent.mm:id/dka";

    /**
     * 微信点击搜索图标后搜索输入框
     */
    String EDT_SEARCH = "com.tencent.mm:id/bfl";

    /**
     * 已有好友详情页的.音视频通话按钮id
     * 用于判断 是否是已经是好友
     */
    String BTN_VIDEO = "com.tencent.mm:id/f00";

    /**
     * 输入搜索内容后.自动弹出的查找xxx按钮
     */
    String BTN_SEARCH = "com.tencent.mm:id/f4d";

    /**
     * 申请添加好友页.发送申请按钮
     */
    String BTN_SEND = "com.tencent.mm:id/ch";

    /**
     * 申请添加好友页.申请内容输入框
     */
    String EDT_ADD_HINT = "com.tencent.mm:id/f1b";

    /**
     * 非home页.有返回按钮的页面的.返回按钮的id
     */
    String IV_BACK = "com.tencent.mm:id/rn";


}
