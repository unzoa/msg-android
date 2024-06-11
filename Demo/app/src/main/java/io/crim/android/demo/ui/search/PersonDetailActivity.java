package io.crim.android.demo.ui.search;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import io.crim.android.sdk.CRIMClient;
import io.crim.android.sdk.enums.GrpRole;
import io.crim.android.sdk.listener.OnBase;
import io.crim.android.sdk.models.FriendshipInfo;
import io.crim.android.sdk.models.GroupMembersInfo;
import io.crim.android.sdk.models.GrpInfo;
import io.crim.android.sdk.models.SignalingInfo;
import io.crim.android.sdk.models.UserInfo;
import io.crim.android.demo.databinding.ActivityPersonDetailBinding;
import io.crim.android.demo.ui.user.PersonDataActivity;
import io.crim.android.demo.vm.FriendVM;
import io.crim.android.ouiconversation.vm.ChatVM;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.base.BaseApp;
import io.crim.android.ouicore.im.IMUtil;
import io.crim.android.ouicore.services.CallingService;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.Obs;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.utils.TimeUtil;
import io.crim.android.ouicore.vm.SearchVM;
import io.crim.android.ouicore.widget.WaitDialog;

@Route(path = Routes.Main.PERSON_DETAIL)
public class PersonDetailActivity extends BaseActivity<SearchVM, ActivityPersonDetailBinding> implements Observer {
    //聊天窗口对象正是此人信息
    private boolean formChat;

    private FriendVM friendVM = new FriendVM();
    private WaitDialog waitDialog;
    private FriendshipInfo friendshipInfo;
    //表示群成员详情  群id
    private String groupId;
    // 不允许查看群成员资料
    private boolean notLookMemberInfo;
    // 不允许添加组成员为好友
    private boolean applyMemberFriend;
    //已经是好友
    private boolean isFriend;
    private CallingService callingService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(SearchVM.class);
        bindViewDataBinding(ActivityPersonDetailBinding.inflate(getLayoutInflater()));
        super.onCreate(savedInstanceState);
        sink();
        init();

        listener();
        click();
    }


    private void init() {
        callingService =
            (CallingService) ARouter.getInstance().build(Routes.Service.CALLING).navigation();
        formChat = getIntent().getBooleanExtra(Constant.K_RESULT, false);
        groupId = getIntent().getStringExtra(Constant.K_GROUP_ID);
        vm.searchContent.setValue(getIntent().getStringExtra(Constant.K_ID));

        Obs.inst().addObserver(this);
        waitDialog = new WaitDialog(this);
        friendVM.waitDialog = waitDialog;
        friendVM.setContext(this);
        friendVM.setIView(this);
        waitDialog.show();

        vm.searchPerson();
    }

    private ActivityResultLauncher<Intent> jumpCallBack =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                getOneselfAndTargetMemberInfo();
            }
        });

    /**
     * 获取自己和选择用户的MemberInfo
     */
    private void getOneselfAndTargetMemberInfo() {
        List<String> ids = new ArrayList<>();
        ids.add(BaseApp.inst().loginCertificate.userID);
        ids.add(vm.searchContent.getValue());
        getGroupMembersInfo(ids, result -> {
            if (result.isEmpty()) return;
            GroupMembersInfo oneselfGroupMembersInfo = null;
            GroupMembersInfo targetGroupMembersInfo = null;
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getUserID().equals(BaseApp.inst().loginCertificate.userID)) {
                    oneselfGroupMembersInfo = result.get(i);
                } else targetGroupMembersInfo = result.get(i);
            }
            if (oneself()) targetGroupMembersInfo = oneselfGroupMembersInfo;
            if (null == oneselfGroupMembersInfo || null == targetGroupMembersInfo) return;
            view.groupNickName.setText(targetGroupMembersInfo.getNickname());
            view.time.setText(TimeUtil.getTime(targetGroupMembersInfo.getJoinTime(),
                TimeUtil.yearMonthDayFormat));
            view.joinMethodLy.setVisibility(oneself() ? View.GONE : View.VISIBLE);

            String muteTime = "";
            if (targetGroupMembersInfo.getMuteEndTime() != 0)
                muteTime = TimeUtil.getTime(targetGroupMembersInfo.getMuteEndTime(),
                    TimeUtil.yearTimeSecondFormat);

            if (targetGroupMembersInfo.getJoinSource() == 2) {
                List<String> ids2 = new ArrayList<>();
                ids2.add(targetGroupMembersInfo.getInviterUserID());
                getGroupMembersInfo(ids2,
                    result2 -> view.joinMethod.setText(result2.get(0).getNickname() + getString(io.crim.android.ouicore.R.string.inviter_join)));
            }
            if (targetGroupMembersInfo.getJoinSource() == 3) {
                view.joinMethod.setText(io.crim.android.ouicore.R.string.search_id_join);
            }
            if (targetGroupMembersInfo.getJoinSource() == 4) {
                view.joinMethod.setText(io.crim.android.ouicore.R.string.group_qr_join);
            }
        });
    }

    private ActivityResultLauncher<Intent> personDataActivityLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) onSuccess(null);
        });

    private void click() {

        view.userInfo.setOnClickListener(v -> {
            personDataActivityLauncher.launch(new Intent(this, PersonDataActivity.class).putExtra(Constant.K_ID, vm.userInfo.getValue().get(0).getUserID()));
        });
        view.sendMsg.setOnClickListener(v -> {
            if (!formChat) {
                ChatVM chatVM = BaseApp.inst().getVMByCache(ChatVM.class);
                if (null != chatVM) {
                    AppCompatActivity compatActivity = (AppCompatActivity) chatVM.getContext();
                    compatActivity.finish();
                    overridePendingTransition(0, 0);
                }
                runOnUiThread(() -> ARouter.getInstance().build(Routes.Conversation.CHAT)
                    .withString(Constant.K_ID, vm.searchContent.getValue())
                    .withString(Constant.K_NAME, vm.userInfo.getValue().get(0).getNickname()).navigation());
            }
            setResult(RESULT_OK);
            finish();
        });


        view.addFriend.setOnClickListener(v -> {
            startActivity(new Intent(this, SendVerifyActivity.class).putExtra(Constant.K_ID,
                vm.searchContent.getValue()));
        });


        view.call.setOnClickListener(v -> {
            if (null == callingService) return;
            IMUtil.showBottomPopMenu(this, (v1, keyCode, event) -> {
                List<String> ids = new ArrayList<>();
                ids.add(vm.searchContent.getValue());
                SignalingInfo signalingInfo = IMUtil.buildSignalingInfo(keyCode != 1, true, ids,
                    null);
                callingService.call(signalingInfo);
                return false;
            });
        });
    }

    @Override
    public void onSuccess(Object body) {
        super.onSuccess(body);
        setResult(RESULT_OK);
        finish();
    }

    boolean oneself() {
        try {
            return BaseApp.inst().loginCertificate.userID.equals(vm.searchContent.getValue());
        } catch (Exception ignored) {
        }
        return false;
    }

    private void listener() {
        vm.groupsInfo.observe(this, groupInfos -> {
            if (groupInfos.isEmpty()) return;

            GrpInfo groupInfo = groupInfos.get(0);
            // 不允许查看群成员资料
            if (notLookMemberInfo = (groupInfo.getLookMemberInfo() == 1)) {
                view.userInfo.setVisibility(View.GONE);
                view.userId.setVisibility(View.GONE);
            } else {
                view.userId.setVisibility(View.VISIBLE);
                if (!oneself() && isFriend) view.userInfo
                    .setVisibility(View.VISIBLE);
            }
            // 不允许添加组成员为好友
            applyMemberFriend = groupInfo.getApplyMemberFriend() == 1;

            view.addFriend.setVisibility((applyMemberFriend || isFriend||oneself()) ? View.GONE :
                View.VISIBLE);
            view.userId.setVisibility(applyMemberFriend ? View.GONE : View.VISIBLE);

            getOneselfAndTargetMemberInfo();
        });

        friendVM.blackListUser.observe(this, userInfos -> {
            boolean isCon = false;
            for (UserInfo userInfo : userInfos) {
                if (userInfo.getUserID().equals(vm.searchContent.getValue())) {
                    isCon = true;
                    break;
                }
            }
            if (null != friendshipInfo) {
                if (friendshipInfo.getResult() == 1 || isCon) {
                    view.userInfo.setVisibility(View.VISIBLE);
                    view.addFriend.setVisibility(View.GONE);
                    isFriend = true;
                } else {
                    view.userInfo.setVisibility(View.GONE);
                    view.addFriend.setVisibility(View.VISIBLE);
                }
            }
            if (!TextUtils.isEmpty(groupId)) {
                vm.searchGroup(groupId);
                view.groupInfo.setVisibility(View.VISIBLE);
            }

            boolean allowSendMsgNotFriend = BaseApp.inst().loginCertificate.allowSendMsgNotFriend;
            view.sendMsg.setVisibility(isFriend || allowSendMsgNotFriend ? View.VISIBLE :
                View.GONE);
//            view.call.setVisibility(isFriend || allowSendMsgNotFriend ? View.VISIBLE : View.GONE);
        });
        vm.userInfo.observe(this, v -> {
            if (null != v && !v.isEmpty()) {
                vm.checkFriend(v);

                UserInfo userInfo = v.get(0);
                String nickName = userInfo.getNickname();
                if (!TextUtils.isEmpty(userInfo.getRemark())) {
                    nickName += "(" + userInfo.getRemark() + ")";
                }
                view.nickName.setText(nickName);
                view.userId.setText(userInfo.getUserID());
                view.avatar.load(userInfo.getFaceURL());
                view.bottomMenu.setVisibility(oneself() ? View.GONE : View.VISIBLE);
            }
        });
        vm.friendshipInfo.observe(this, v -> {
            if (null != v && !v.isEmpty()) {
                friendshipInfo = v.get(0);
                friendVM.getBlacklist();
            }
        });
    }

    void getGroupMembersInfo(List<String> ids,
                             IMUtil.OnSuccessListener<List<GroupMembersInfo>> successListener) {
        CRIMClient.getInstance().groupManager.getSpecifiedGrpMembersInfo(new OnBase<List<GroupMembersInfo>>() {
            @Override
            public void onError(int code, String error) {
                toast(error + code);
            }

            @Override
            public void onSuccess(List<GroupMembersInfo> data) {
                if (data.isEmpty()) return;
                successListener.onSuccess(data);
            }
        }, groupId, ids);
    }

    private boolean isOwner(GroupMembersInfo membersInfo) {
        return membersInfo.getRoleLevel() == GrpRole.OWNER;
    }

    private boolean isAdmin(GroupMembersInfo membersInfo) {
        return membersInfo.getRoleLevel() == GrpRole.ADMIN;
    }


    @Override
    public void update(Observable o, Object arg) {
        Obs.Msg message = (Obs.Msg) arg;
        if (message.tag == Constant.Event.USER_INFO_UPDATE) {
            vm.searchPerson();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Obs.inst().deleteObserver(this);
    }
}
