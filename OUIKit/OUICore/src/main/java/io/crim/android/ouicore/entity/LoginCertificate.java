package io.crim.android.ouicore.entity;

import android.content.Context;

import io.crim.android.ouicore.base.BaseApp;
import io.crim.android.ouicore.net.bage.GsonHel;
import io.crim.android.ouicore.utils.SharedPreferencesUtil;

public class LoginCertificate {
    public String nickName;
    public String faceURL;
    public String userID;
    public String imToken;
    public String chatToken;

    public boolean allowSendMsgNotFriend;
    public boolean allowAddFriend;

    //允许响铃
    public boolean allowBeep;
    //允许振动
    public boolean allowVibration;
    // 全局免打扰 0：正常；1：不接受消息；2：接受在线消息不接受离线消息；
    public int globalRecvMsgOpt;

    public void cache(Context context) {
        SharedPreferencesUtil.get(context).setCache("user.LoginCertificate",
            GsonHel.toJson(this));
    }

    public static LoginCertificate getCache(Context context) {
        String u = SharedPreferencesUtil.get(context).getString("user.LoginCertificate");
        if (u.isEmpty()) return null;
        return GsonHel.fromJson(u, LoginCertificate.class);
    }

    public static void clear() {
        SharedPreferencesUtil.remove(BaseApp.inst(),
            "user.LoginCertificate");
    }

}
