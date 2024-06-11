package io.crim.android.ouigroup.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.crim.android.ouicore.adapter.RecyclerViewAdapter;
import io.crim.android.ouicore.adapter.ViewHol;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.base.BaseApp;
import io.crim.android.ouicore.databinding.LayoutMemberActionBinding;
import io.crim.android.ouicore.entity.ExGroupMemberInfo;
import io.crim.android.ouicore.net.bage.GsonHel;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.vm.GroupVM;
import io.crim.android.ouicore.widget.CommonDialog;
import io.crim.android.ouigroup.databinding.ActivitySuperGroupMemberBinding;
import io.crim.android.sdk.enums.GrpRole;
import io.crim.android.sdk.models.GroupMembersInfo;

@Route(path = Routes.Group.SUPER_GROUP_MEMBER)
public class SuperGroupMemberActivity extends BaseActivity<GroupVM,
    ActivitySuperGroupMemberBinding> {
    private RecyclerViewAdapter adapter;
    //转让群主权限
    private boolean isTransferPermission;
    //选择群成员
    private boolean isSelectMember;
    private int maxNum;
    private int selectNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVMByCache(GroupVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivitySuperGroupMemberBinding.inflate(getLayoutInflater()));
        sink();

        init();
        initView();
        listener();
    }

    private ActivityResultLauncher<Intent> searchFriendLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            try {
                String uid = result.getData().getStringExtra(Constant.K_ID);
                if (isSelectMember) {
                    String resultJson = result.getData().getStringExtra(Constant.K_RESULT);
                    GroupMembersInfo groupMembersInfo = GsonHel.fromJson(resultJson,
                        GroupMembersInfo.class);
                    ExGroupMemberInfo exGroupMemberInfo = new ExGroupMemberInfo();
                    exGroupMemberInfo.isSelect = true;
                    exGroupMemberInfo.groupMembersInfo = groupMembersInfo;
                    int index = vm.superGroupMembers.getValue().indexOf(exGroupMemberInfo);
                    if (index != -1) {
                        vm.superGroupMembers.getValue().get(index).isSelect = true;
                    } else {
                        vm.superGroupMembers.getValue().add(exGroupMemberInfo);
                    }
                    adapter.notifyItemChanged(index);
                    updateSelectedNum();
                } else {
                    ARouter.getInstance().build(Routes.Main.PERSON_DETAIL).withString(Constant.K_ID,
                        uid).withString(Constant.K_GROUP_ID, vm.groupId).navigation();
                }
            } catch (Exception ignored) {

            }
        });
    private ActivityResultLauncher<Intent> selectMemberLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                adapter.notifyDataSetChanged();
                updateSelectedNum();
            }
        });

    private void updateSelectedNum() {
        selectNum = 0;
        for (ExGroupMemberInfo exGroupMemberInfo : vm.superGroupMembers.getValue()) {
            if (exGroupMemberInfo.isSelect) selectNum++;
        }
        view.bottomLayout.selectNum.setText(String.format(getString(io.crim.android.ouicore.R.string.selected_tips), selectNum));
    }

    void init() {
        if (vm.superGroupMembers.getValue().size() > Constant.SUPER_GROUP_LIMIT) {
            vm.superGroupMembers.getValue().clear();
            vm.page = 0;
            vm.pageSize = 20;
        }
        isTransferPermission = getIntent().getBooleanExtra(Constant.K_FROM, false);
        isSelectMember = getIntent().getBooleanExtra(Constant.IS_SELECT_MEMBER, false);
        maxNum = getIntent().getIntExtra(Constant.K_SIZE, 9);
    }

    private void listener() {
        view.bottomLayout.submit.setOnClickListener(v -> {
            ArrayList<String> ids = new ArrayList<>();
            for (ExGroupMemberInfo exGroupMemberInfo : vm.superGroupMembers.getValue()) {
                if (exGroupMemberInfo.isSelect)
                    ids.add(exGroupMemberInfo.groupMembersInfo.getUserID());
            }
            if (ids.size() == 1 && ids.get(0).equals(BaseApp.inst().loginCertificate.userID)) {
                toast(getString(io.crim.android.ouicore.R.string.group_call_tips3));
                return;
            }
            setResult(RESULT_OK, new Intent().putStringArrayListExtra(Constant.K_RESULT, ids));
            finish();

            BaseApp.inst().removeCacheVM(GroupVM.class);
        });
        view.searchView.setOnClickListener(v -> {
            Postcard postcard = ARouter.getInstance().build(Routes.Contact.SEARCH_FRIENDS);
            LogisticsCenter.completion(postcard);
            searchFriendLauncher.launch(new Intent(this, postcard.getDestination()).putExtra(Constant.K_GROUP_ID, vm.groupId));
        });
        view.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager =
                    (LinearLayoutManager) view.recyclerview.getLayoutManager();
                int lastVisiblePosition =
                    linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == adapter.getItems().size() - 1 && adapter.getItems().size() >= vm.pageSize) {
                    vm.page++;
                    loadMember();
                }
            }
        });
        view.more.setOnClickListener(v -> {
            PopupWindow popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutMemberActionBinding view = LayoutMemberActionBinding.inflate(getLayoutInflater());
            view.deleteFriend.setVisibility(vm.isOwner() ? View.VISIBLE : View.GONE);
            view.addFriend.setOnClickListener(v1 -> {
                popupWindow.dismiss();
                startActivity(new Intent(this, InitiateGroupActivity.class).putExtra(Constant.IS_INVITE_TO_GROUP, true));
            });
            view.deleteFriend.setOnClickListener(v1 -> {
                popupWindow.dismiss();
                startActivity(new Intent(this, InitiateGroupActivity.class).putExtra(Constant.IS_REMOVE_GROUP, true));
            });
            //设置PopupWindow的视图内容
            popupWindow.setContentView(view.getRoot());
            //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popupWindow.setOutsideTouchable(true);

            //设置PopupWindow消失监听
            popupWindow.setOnDismissListener(() -> {

            });
            //PopupWindow在targetView下方弹出
            popupWindow.showAsDropDown(v);
        });

        vm.superGroupMembers.observe(this, v -> {
            if (v.isEmpty()) return;
            updateSelectedNum();
            if (v.size() > vm.pageSize) {
                adapter.notifyItemRangeInserted(vm.superGroupMembers.getValue().size()
                    - vm.pageSize, vm.superGroupMembers.getValue().size());
            } else {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadMember() {
        vm.getSuperGroupMemberList();
    }

    private void initView() {
        view.more.setVisibility(isTransferPermission ? View.GONE : View.VISIBLE);
        view.bottomLayout.getRoot().setVisibility(isSelectMember ? View.VISIBLE : View.GONE);
        view.recyclerview.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RecyclerViewAdapter<ExGroupMemberInfo, RecyclerView.ViewHolder>() {


            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
                return new ViewHol.ItemViewHo(parent);
            }

            @Override
            public void onBindView(@NonNull RecyclerView.ViewHolder holder,
                                   ExGroupMemberInfo data, int position) {
                ViewHol.ItemViewHo itemViewHo = (ViewHol.ItemViewHo) holder;
                itemViewHo.view.select.setVisibility(isSelectMember ? View.VISIBLE : View.GONE);
                itemViewHo.view.select.setChecked(data.isSelect);
                itemViewHo.view.avatar.load(data.groupMembersInfo.getFaceURL());
                itemViewHo.view.nickName.setText(data.groupMembersInfo.getNickname());
                if (data.groupMembersInfo.getRoleLevel() == GrpRole.OWNER) {
                    itemViewHo.view.identity.setVisibility(View.VISIBLE);
                    itemViewHo.view.identity.setBackgroundResource(io.crim.android.ouicore.R.drawable.sty_radius_8_fddfa1);
                    itemViewHo.view.identity.setText(io.crim.android.ouicore.R.string.lord);
                    itemViewHo.view.identity.setTextColor(Color.parseColor("#ffff8c00"));
                } else if (data.groupMembersInfo.getRoleLevel() == GrpRole.ADMIN) {
                    itemViewHo.view.identity.setVisibility(View.VISIBLE);
                    itemViewHo.view.identity.setBackgroundResource(io.crim.android.ouicore.R.drawable.sty_radius_8_a2c9f8);
                    itemViewHo.view.identity.setText(io.crim.android.ouicore.R.string.administrator);
                    itemViewHo.view.identity.setTextColor(Color.parseColor("#2691ED"));
                } else itemViewHo.view.identity.setVisibility(View.GONE);


                itemViewHo.view.getRoot().setOnClickListener(v -> {
                    if (isSelectMember) {
                        if (!data.isEnabled) {
                            toast(getString(io.crim.android.ouicore.R.string.group_call_tips));
                            return;
                        }
                        boolean isSelect = !data.isSelect;
                        if (selectNum >= maxNum && isSelect) {
                            toast(String.format(getString(io.crim.android.ouicore.R.string.select_tips), maxNum));
                            return;
                        }
                        data.isSelect = isSelect;
                        if (isSelect) selectNum++;
                        else selectNum--;
                        notifyItemChanged(position);
                        view.bottomLayout.more2.setVisibility(View.VISIBLE);
                        view.bottomLayout.selectNum.setText(String.format(getString(io.crim.android.ouicore.R.string.selected_tips), selectNum));
                        view.bottomLayout.submit.setEnabled(selectNum > 0);
                        view.bottomLayout.selectLy.setOnClickListener(selectNum > 0 ?
                            (View.OnClickListener) v1 -> {
                                selectMemberLauncher.launch(new Intent(SuperGroupMemberActivity.this,
                                    SelectedMemberActivity.class));
                            } : null);
                        return;
                    }
                    if (isTransferPermission) {
                        if (data.groupMembersInfo.getRoleLevel() == 2)
                            toast(BaseApp.inst().getString(io.crim.android.ouicore.R.string.repeat_group_manager));
                        else {
                            CommonDialog commonDialog =
                                new CommonDialog(SuperGroupMemberActivity.this);
                            commonDialog.getMainView().tips.setText(String.format(BaseApp.inst().getString(io.crim.android.ouicore.R.string.transfer_permission), data.groupMembersInfo.getNickname()));
                            commonDialog.getMainView().cancel.setOnClickListener(v2 -> {
                                commonDialog.dismiss();
                            });
                            commonDialog.getMainView().confirm.setOnClickListener(v2 -> {
                                commonDialog.dismiss();
                                vm.transferGroupOwner(data.groupMembersInfo.getUserID(), data1 -> {
                                    toast(getString(io.crim.android.ouicore.R.string.transfer_succ));
                                    finish();
                                });
                            });
                            commonDialog.show();
                        }
                    } else {
                        ARouter.getInstance().build(Routes.Main.PERSON_DETAIL).withString(Constant.K_ID, data.groupMembersInfo.getUserID()).withString(Constant.K_GROUP_ID, vm.groupId).navigation();
                    }
                });


            }
        };
        adapter.setItems(vm.superGroupMembers.getValue());
        view.recyclerview.setAdapter(adapter);

        vm.superGroupMembers.getValue().clear();
        loadMember();
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

}
