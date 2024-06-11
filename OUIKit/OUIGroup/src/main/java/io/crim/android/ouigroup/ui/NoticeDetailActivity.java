package io.crim.android.ouigroup.ui;

import android.os.Bundle;


import com.alibaba.android.arouter.facade.annotation.Route;

import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.entity.ExGroupMemberInfo;
import io.crim.android.ouicore.entity.NotificationMsg;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.utils.TimeUtil;
import io.crim.android.ouigroup.databinding.ActivityNoticeDetailBinding;

import io.crim.android.ouicore.vm.GroupVM;


@Route(path = Routes.Group.NOTICE_DETAIL)
public class NoticeDetailActivity extends BaseActivity<GroupVM, ActivityNoticeDetailBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(GroupVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityNoticeDetailBinding.inflate(getLayoutInflater()));
        sink();
        NotificationMsg notificationMsg = (NotificationMsg) getIntent().getSerializableExtra(Constant.K_NOTICE);
        vm.groupId=notificationMsg.group.groupID;
        vm.getGroupMemberList();
        vm.exGroupMembers.observe(this, exGroupMemberInfos -> {
            if (exGroupMemberInfos.isEmpty()) return;
            if (null != notificationMsg) {
                ExGroupMemberInfo exGroupMemberInfo = vm.getOwnInGroup(notificationMsg.group.ownerUserID);
                if (null==exGroupMemberInfo)return;
                view.avatar.load(exGroupMemberInfo.groupMembersInfo.getFaceURL());
                view.name.setText(exGroupMemberInfo.groupMembersInfo.getNickname());
                view.time.setText(TimeUtil.getTime(notificationMsg.group.createTime* 1000L, TimeUtil.yearTimeFormat));
                view.content.setText(notificationMsg.group.notification);
            }
        });
    }


}
