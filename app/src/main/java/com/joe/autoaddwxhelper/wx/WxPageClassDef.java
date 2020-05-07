package com.joe.autoaddwxhelper.wx;


import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description：
 *
 * @author QiaoJF on 2020/4/28.
 */
@StringDef({ WxPageClassDef.LAUNCHER, WxPageClassDef.ADD_FRIEND, WxPageClassDef.ADD_FRIEND_MORE , WxPageClassDef.SEARCH, WxPageClassDef.CONTACT, WxPageClassDef.SAY_HI2ADD, WxPageClassDef.LOADING })
@Retention(RetentionPolicy.SOURCE)
public @interface WxPageClassDef {

    /**
     * 微信Home页
     */
    String LAUNCHER = "com.tencent.mm.ui.LauncherUI";

    /**
     * 添加朋友页
     */
    String ADD_FRIEND = "com.tencent.mm.plugin.fts.ui.FTSAddFriendUI";

    /**
     * 添加朋友->搜索页
     */
    String ADD_FRIEND_MORE = "com.tencent.mm.plugin.subapp.ui.pluginapp.AddMoreFriendsUI";

    /**
     * 搜索按钮-搜索页
     */
    String SEARCH = "com.tencent.mm.plugin.fts.ui.FTSMainUI";

    /**
     * 搜索到未添加的联系人详情页
     */
    String CONTACT = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";

    /**
     * 申请添加朋友页
     */
    String SAY_HI2ADD = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";

    /**
     * 点击添加通讯录后出发的loading页.避免触发返回操作
     */
    String LOADING = "com.tencent.mm.ui.base.p";

}
