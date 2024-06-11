package io.crim.android.ouigroup.ui.v3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.crim.android.sdk.enums.GrpStatus;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.base.vm.injection.Easy;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.vm.GroupVM;
import io.crim.android.ouicore.widget.BottomPopDialog;
import io.crim.android.ouigroup.databinding.ActivityGroupManageBinding;
import io.crim.android.ouigroup.ui.MemberPermissionActivity;
import io.crim.android.ouigroup.ui.SuperGroupMemberActivity;

public class GroupManageActivity extends BaseActivity<GroupVM, ActivityGroupManageBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm= Easy.find(GroupVM.class);
        bindViewDataBinding(ActivityGroupManageBinding.inflate(getLayoutInflater()));
        view.setGroupVM(vm);

        click();
        listener();
    }
    private void listener() {
        vm.groupsInfo.observe(this, groupInfo -> {
            view.totalSilence.setCheckedWithAnimation(groupInfo.getStatus() == GrpStatus.GROUP_MUTED);
            view.describe.setText(getJoinGroupOption(groupInfo.getNeedVerification()));
        });
    }
    String getJoinGroupOption(int value) {
        if (value == Constant.GroupVerification.allNeedVerification) {
            return getString(io.crim.android.ouicore.R.string.needVerification);
        } else if (value == Constant.GroupVerification.directly) {
            return getString(io.crim.android.ouicore.R.string.allowAnyoneJoinGroup);
        }
        return getString(io.crim.android.ouicore.R.string.inviteNotVerification);
    }
    private void click() {
        view.memberPermissions.setOnClickListener(v -> {
            startActivity(new Intent(this, MemberPermissionActivity.class));
        });
        view.joinValidation.setOnClickListener(v -> {
            BottomPopDialog dialog = new BottomPopDialog(this);
            dialog.show();
            dialog.getMainView().menu3.setOnClickListener(v1 -> dialog.dismiss());
            dialog.getMainView().menu1.setText(io.crim.android.ouicore.R.string.allowAnyoneJoinGroup);
            dialog.getMainView().menu2.setText(io.crim.android.ouicore.R.string.inviteNotVerification);
            dialog.getMainView().menu4.setVisibility(View.VISIBLE);
            dialog.getMainView().menu4.setText(io.crim.android.ouicore.R.string.needVerification);

            dialog.getMainView().menu1.setOnClickListener(v1 -> {
                dialog.dismiss();
                vm.setGroupVerification(Constant.GroupVerification.directly, data -> {
                    vm.groupsInfo.getValue().setNeedVerification(Constant.GroupVerification.directly);
                    vm.groupsInfo.setValue(vm.groupsInfo.getValue());
                });
            });
            dialog.getMainView().menu2.setOnClickListener(v1 -> {
                dialog.dismiss();
                vm.setGroupVerification(Constant.GroupVerification.applyNeedVerificationInviteDirectly, data -> {
                    vm.groupsInfo.getValue().setNeedVerification(Constant.GroupVerification.applyNeedVerificationInviteDirectly);
                    vm.groupsInfo.setValue(vm.groupsInfo.getValue());
                });
            });
            dialog.getMainView().menu4.setOnClickListener(v1 -> {
                dialog.dismiss();
                vm.setGroupVerification(Constant.GroupVerification.allNeedVerification, data -> {
                    vm.groupsInfo.getValue().setNeedVerification(Constant.GroupVerification.allNeedVerification);
                    vm.groupsInfo.setValue(vm.groupsInfo.getValue());
                });
            });
        });
        view.totalSilence.setOnSlideButtonClickListener(isChecked -> {
            vm.changeGroupMute(isChecked, data -> {
                view.totalSilence.setCheckedWithAnimation(isChecked);
            });
        });

        view.transferPermissions.setOnClickListener(v -> {
            gotoMemberList(true);
        });
    }

    private void gotoMemberList(boolean transferPermissions) {
//        if (vm.groupMembers.getValue().isEmpty()) return;
//        if (vm.groupMembers.getValue().size() > Constant.SUPER_GROUP_LIMIT)
        startActivity(new Intent(GroupManageActivity.this, SuperGroupMemberActivity.class).putExtra(Constant.K_FROM, transferPermissions));
//        else
//            startActivity(new Intent(GroupMaterialActivity.this, GroupMemberActivity.class)
//            .putExtra(Constant.K_FROM, transferPermissions));
    }
}
