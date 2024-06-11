package io.crim.android.ouicore.utils;

import android.text.TextUtils;

import io.crim.android.ouicore.AccountConfig;
import io.crim.android.ouicore.base.BaseApp;
import io.crim.android.ouicore.im.IM;

public class Constant {
//  public static final String DEFAULT_IP = "test-web.rentsoft.cn";//43
//  public static final String DEFAULT_IP = "web.rentsoft.cn";//121

    public static final String DEFAULT_IP = "demo.cloudroom.com";//121
//    public static final String DEFAULT_IP = "203.56.175.233";//121
//    public static final String DEFAULT_IP = "43.154.157.177";//43
//    public static final String DEFAULT_IP = "59.36.173.89";//43

    private static final String SERVER_IP = "sdk.cloudroom.com";
//    private static final String APP_ID = "demo@cloudroom.com";

    //登录注册手机验 证服务器地址
    private static final String APP_AUTH_URL = "https://" + DEFAULT_IP + "/chat/";
    //IM sdk api地址
    private static final String IM_API_URL = "https://" + DEFAULT_IP + "/api";
    //web socket
    private static final String IM_WS_URL = "wss://" + DEFAULT_IP + "/msg_gateway";

    //--------IP----------
    private static final String APP_AUTH_PORT = "8018";
    private static final String IM_API_PORT = "10002";
    private static final String IM_WS_PORT = "10001";
    private static final String APP_AUTH = "http://" + DEFAULT_IP + ":" + APP_AUTH_PORT + "/";
    private static final String IM_API = "http://" + DEFAULT_IP + ":" + IM_API_PORT;
    private static final String IM_WS = "ws://" + DEFAULT_IP + ":" + IM_WS_PORT;
    private static final Boolean isIP = true;
    //--------------------

    public static String getBusinessServer() {
        String ip = SharedPreferencesUtil.get(BaseApp.inst()).getString("DEFAULT_IP");
        if (!TextUtils.isEmpty(ip)) {
            return ip;
        }
        return DEFAULT_IP;
    }

    public static String getSdkServer() {
        String url = SharedPreferencesUtil.get(BaseApp.inst()).getString("SERVER_IP");
        if (!TextUtils.isEmpty(url)) {
            return url;
        }
        return SERVER_IP;
    }

    public static String getSdkServerUrl() {
        return "http://" + getSdkServer();
    }

    public static String getAppID() {
        String appId = SharedPreferencesUtil.get(BaseApp.inst()).getString("APP_ID");
        if (!TextUtils.isEmpty(appId)) {
            return appId;
        }
        return AccountConfig.APP_ID;
    }

    public static String getAppSecret() {
        String appSecret = SharedPreferencesUtil.get(BaseApp.inst()).getString("APP_SECRET");
        if (!TextUtils.isEmpty(appSecret)) {
            return appSecret;
        }
        return AccountConfig.APP_SECRET;
    }

    public static String getImApiUrl() {
        String url = SharedPreferencesUtil.get(BaseApp.inst()).getString("IM_API_URL");
        if (TextUtils.isEmpty(url)) return isIP ? IM_API : IM_API_URL;
        return url;
    }

    public static String getAppAuthUrl() {
        String url = SharedPreferencesUtil.get(BaseApp.inst()).getString("APP_AUTH_URL");
        if (TextUtils.isEmpty(url)) return isIP ? APP_AUTH : APP_AUTH_URL;
        return url;
    }

    public static String getImWsUrl() {
        String url = SharedPreferencesUtil.get(BaseApp.inst()).getString("IM_WS_URL");
        if (TextUtils.isEmpty(url)) return isIP ? IM_WS : IM_WS_URL;
        return url;
    }

    public static String getStorageType() {
        String url = SharedPreferencesUtil.get(BaseApp.inst()).getString("STORAGE_TYPE");
        if (TextUtils.isEmpty(url)) return "minio";
        return url;
    }

    public static void saveUrl() {
        String imApi = "";
        String appAuth = "";
        String imWs = "";
        String businessServer = getBusinessServer();
        if (isIP) {
            appAuth = "http://" + businessServer + ":" + APP_AUTH_PORT + "/";
            imApi = "http://" + businessServer + ":" + IM_API_PORT;
            imWs = "ws://" + businessServer + ":" + IM_WS_PORT;
        } else {
            appAuth = "https://" + businessServer + "/chat/";
            imApi = "https://" + businessServer + "/api";
            imWs = "wss://" + businessServer + "/msg_gateway";
        }
        SharedPreferencesUtil.get(BaseApp.inst()).setCache("IM_API_URL", imApi);
        SharedPreferencesUtil.get(BaseApp.inst()).setCache("APP_AUTH_URL", appAuth);
        SharedPreferencesUtil.get(BaseApp.inst()).setCache("IM_WS_URL", imWs);
    }

    //存储音频的文件夹
    public static final String AUDIO_DIR = IM.getStorageDir() + "/audio/";
    //视频存储文件夹
    public static final String VIDEO_DIR = IM.getStorageDir() + "/video/";
    //图片存储文件夹
    public static final String PICTURE_DIR = IM.getStorageDir() + "/picture/";
    //文件夹
    public static final String File_DIR = IM.getStorageDir() + "/file/";

    //二维码
    public static class QR {
        public static final String QR_ADD_FRIEND = "io.crim.app/addFriend";
        public static final String QR_JOIN_GROUP = "io.crim.app/joinGroup";
    }


    public static class Event {
        //转发选人
        public static final int FORWARD = 10002;
        //音视频通话
        public static final int CALLING_REQUEST_CODE = 10003;
        //用户信息更新
        public static final int USER_INFO_UPDATE = 10004;
        //设置背景
        public static final int SET_BACKGROUND = 10005;
        //群信息更新
        public static final int UPDATE_GROUP_INFO = 10006;
        //设置群通知
        public static final int SET_GROUP_NOTIFICATION = 10007;
        //插入了消息到本地
        public static final int INSERT_MSG = 10008;
    }

    public static final String K_ID = "Id";
    public static final String K_GROUP_ID = "group_id";
    public static final String K_IS_PERSON = "is_person";
    public static final String K_NOTICE = "notice";
    public static final String K_NAME = "name";
    public static final String K_RESULT = "result";
    public static final String K_RESULT2 = "result2";
    public static final String K_FROM = "from";
    public static final String K_SIZE = "size";
    public static final String NOTICE_TAG = "msg_notification";


    //最大通话人数
    public static final int MAX_CALL_NUM = 9;
    //好友红点
    public static final String K_FRIEND_NUM = "k_friend_num";
    //群红点
    public static final String K_GROUP_NUM = "k_group_num";
    public static final String K_SET_BACKGROUND = "set_background";

    //邀请入群
    public static final String IS_INVITE_TO_GROUP = "isInviteToGroup";
    //移除群聊
    public static final String IS_REMOVE_GROUP = "isRemoveGroup";
    //选择群成员
    public static final String IS_SELECT_MEMBER = "isSelectMember";
    //选择好友
    public static final String IS_SELECT_FRIEND = "isSelectFriend";
    //自定义消息类型
    public static final String K_CUSTOM_TYPE = "customType";


    //加载中
    public static final int LOADING = 201;

    public static class MsgType {
        //本地呼叫记录
        public static final int LOCAL_CALL_HISTORY = -110;

        //会议邀请
        public static final int CUSTOMIZE_MEETING = 905;

    }


    /// 进群验证设置选项
    public static class GroupVerification {
        /// 申请需要同意 邀请直接进
        public static final int applyNeedVerificationInviteDirectly = 0;

        /// 所有人进群需要验证，除了群主管理员邀
        public static final int allNeedVerification = 1;

        /// 直接进群
        public static final int directly = 2;
    }

    //超级群
    public static final int SUPER_GROUP_LIMIT = 250;

    public static class MediaType {
        public static final String VIDEO = "video";
        public static final String AUDIO = "audio";
    }
}
