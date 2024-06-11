//package io.crim.android.ouigroup.ui;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.PopupWindow;
//
//import com.alibaba.android.arouter.launcher.ARouter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.crim.android.ouicore.adapter.RecyclerViewAdapter;
//import io.crim.android.ouicore.adapter.ViewHol;
//import io.crim.android.ouicore.base.BaseActivity;
//import io.crim.android.ouicore.base.BaseApp;
//import io.crim.android.ouicore.databinding.LayoutMemberActionBinding;
//import io.crim.android.ouicore.entity.ExGroupMemberInfo;
//import io.crim.android.ouicore.utils.Common;
//import io.crim.android.ouicore.utils.Constant;
//import io.crim.android.ouicore.utils.Routes;
//import io.crim.android.ouicore.widget.CommonDialog;
//import io.crim.android.ouigroup.databinding.ActivityGroupMemberBinding;
//
//import io.crim.android.ouicore.vm.GroupVM;
//import io.crim.android.sdk.models.GroupMembersInfo;
//
//public class GroupMemberActivity extends BaseActivity<GroupVM, ActivityGroupMemberBinding> {
//    private RecyclerViewAdapter adapter;
//    //转让群主权限
//    private boolean isTransferPermission;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        bindVMByCache(GroupVM.class);
//        super.onCreate(savedInstanceState);
//        bindViewDataBinding(ActivityGroupMemberBinding.inflate(getLayoutInflater()));
//        sink();
//
//        init();
//        initView();
//        listener();
//    }
//
//    void init() {
//        isTransferPermission = getIntent().getBooleanExtra(Constant.K_FROM, false);
//    }
//
//    private void listener() {
//        view.more.setOnClickListener(v -> {
//            PopupWindow popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//            LayoutMemberActionBinding view = LayoutMemberActionBinding.inflate(getLayoutInflater());
//            view.deleteFriend.setVisibility(vm.isOwner() ? View.VISIBLE : View.GONE);
//            view.addFriend.setOnClickListener(v1 -> {
//                popupWindow.dismiss();
//                startActivity(new Intent(this, InitiateGroupActivity.class).putExtra(Constant.IS_INVITE_TO_GROUP, true));
//            });
//            view.deleteFriend.setOnClickListener(v1 -> {
//                popupWindow.dismiss();
//                startActivity(new Intent(this, InitiateGroupActivity.class).putExtra(Constant.IS_REMOVE_GROUP, true));
//            });
//            //设置PopupWindow的视图内容
//            popupWindow.setContentView(view.getRoot());
//            //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
//            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
//            popupWindow.setOutsideTouchable(true);
//
//            //设置PopupWindow消失监听
//            popupWindow.setOnDismissListener(() -> {
//
//            });
//            //PopupWindow在targetView下方弹出
//            popupWindow.showAsDropDown(v);
//        });
//
//        vm.groupLetters.observe(this, v -> {
//            if (null == v || v.isEmpty()) return;
//            StringBuilder letters = new StringBuilder();
//            for (String s : v) {
//                letters.append(s);
//            }
//            view.sortView.setLetters(letters.toString());
//        });
//
//
//        view.sortView.setOnLetterChangedListener((letter, position) -> {
//            if (letter.equals("↑")) {
//                view.scrollView.smoothScrollTo(0, 0);
//                return;
//            }
//            for (int i = 0; i < adapter.getItems().size(); i++) {
//                ExGroupMemberInfo exGroupMemberInfo = (ExGroupMemberInfo) adapter.getItems().get(i);
//                if (!exGroupMemberInfo.isSticky) continue;
//                if (exGroupMemberInfo.sortLetter.equalsIgnoreCase(letter)) {
//                    View viewByPosition =
//                        view.recyclerView.getLayoutManager().findViewByPosition(i);
//                    if (viewByPosition != null) {
//                        view.scrollView.smoothScrollTo(0, viewByPosition.getTop());
//                    }
//                    return;
//                }
//            }
//        });
//    }
//
//    private void initView() {
//        view.scrollView.fullScroll(View.FOCUS_DOWN);
//        view.recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new RecyclerViewAdapter<ExGroupMemberInfo, RecyclerView.ViewHolder>() {
//            private int STICKY = 1;
//            private int ITEM = 2;
//
//            private String lastSticky = "";
//
//            @Override
//            public void setItems(List<ExGroupMemberInfo> items) {
//                if (!items.isEmpty()){
//                    lastSticky = items.get(0).sortLetter;
//                    items.add(0, getexGroupMemberInfo());
//                }
//                for (int i = 0; i < items.size(); i++) {
//                    ExGroupMemberInfo userInfo = items.get(i);
//                    if (!lastSticky.equals(userInfo.sortLetter)) {
//                        lastSticky = userInfo.sortLetter;
//                        items.add(i, getexGroupMemberInfo());
//                    }
//                }
//                super.setItems(items);
//            }
//
//            @NonNull
//            private ExGroupMemberInfo getexGroupMemberInfo() {
//                ExGroupMemberInfo exGroupMemberInfo = new ExGroupMemberInfo();
//                exGroupMemberInfo.sortLetter = lastSticky;
//                exGroupMemberInfo.isSticky = true;
//                return exGroupMemberInfo;
//            }
//
//            @Override
//            public int getItemViewType(int position) {
//                return getItems().get(position).isSticky ? STICKY : ITEM;
//            }
//
//            @NonNull
//            @Override
//            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
//                                                              int viewType) {
//                if (viewType == ITEM) return new ViewHol.ItemViewHo(parent);
//
//                return new ViewHol.StickyViewHo(parent);
//            }
//
//            @Override
//            public void onBindView(@NonNull RecyclerView.ViewHolder holder,
//                                   ExGroupMemberInfo data, int position) {
//                if (getItemViewType(position) == ITEM) {
//                    ViewHol.ItemViewHo itemViewHo = (ViewHol.ItemViewHo) holder;
//                    GroupMembersInfo friendInfo = data.groupMembersInfo;
//                    itemViewHo.view.avatar.load(friendInfo.getFaceURL());
//                    itemViewHo.view.nickName.setText(friendInfo.getNickname());
//                    itemViewHo.view.select.setVisibility(View.GONE);
//                    if (data.groupMembersInfo.getRoleLevel() == Constant.RoleLevel.GROUP_OWNER) {
//                        itemViewHo.view.identity.setVisibility(View.VISIBLE);
//                        itemViewHo.view.identity.setBackgroundResource(io.crim.android.ouicore.R.drawable.sty_radius_8_fddfa1);
//                        itemViewHo.view.identity.setText(io.crim.android.ouicore.R.string.lord);
//                        itemViewHo.view.identity.setTextColor(Color.parseColor("#ffff8c00"));
//                    } else if (data.groupMembersInfo.getRoleLevel() == Constant.RoleLevel.ADMINISTRATOR) {
//                        itemViewHo.view.identity.setVisibility(View.VISIBLE);
//                        itemViewHo.view.identity.setBackgroundResource(io.crim.android.ouicore.R.drawable.sty_radius_8_a2c9f8);
//                        itemViewHo.view.identity.setText(io.crim.android.ouicore.R.string.administrator);
//                        itemViewHo.view.identity.setTextColor(Color.parseColor("#2691ED"));
//                    } else itemViewHo.view.identity.setVisibility(View.GONE);
//
//
//                    itemViewHo.view.getRoot().setOnClickListener(v -> {
//                        if (isTransferPermission) {
//                            if (data.groupMembersInfo.getRoleLevel() == 2)
//                                toast(BaseApp.inst().getString(io.crim.android.ouicore.R.string.repeat_group_manager));
//                            else {
//                                CommonDialog commonDialog =
//                                    new CommonDialog(GroupMemberActivity.this);
//                                commonDialog.getMainView().tips.setText(String.format(BaseApp.inst().getString(io.crim.android.ouicore.R.string.transfer_permission), data.groupMembersInfo.getNickname()));
//                                commonDialog.getMainView().cancel.setOnClickListener(v2 -> {
//                                    commonDialog.dismiss();
//                                });
//                                commonDialog.getMainView().confirm.setOnClickListener(v2 -> {
//                                    commonDialog.dismiss();
//                                    vm.transferGroupOwner(data.groupMembersInfo.getUserID(),
//                                        data1 -> {
//                                        toast(getString(io.crim.android.ouicore.R.string.transfer_succ));
//                                        finish();
//                                    });
//                                });
//                                commonDialog.show();
//                            }
//                        } else {
//                            ARouter.getInstance().build(Routes.Main.PERSON_DETAIL)
//                                .withString(Constant.K_ID, friendInfo.getUserID()).withString(Constant.K_GROUP_ID, friendInfo.getGroupID()).navigation();
//                        }
//                    });
//
//                } else {
//                    ViewHol.StickyViewHo stickyViewHo = (ViewHol.StickyViewHo) holder;
//                    stickyViewHo.view.title.setText(data.sortLetter);
//                }
//            }
//
//        };
//
//
//        vm.exGroupMembers.observe(this, v -> {
//            List<ExGroupMemberInfo> exGroupMemberInfos = new ArrayList<>();
//            exGroupMemberInfos.addAll(vm.exGroupMembers.getValue());
//            view.recyclerView.setAdapter(adapter);
//            Common.UIHandler.post(() -> {
//                adapter.setItems(exGroupMemberInfos);
//                if (!exGroupMemberInfos.isEmpty())
//                    exGroupMemberInfos.addAll(0, vm.exGroupManagement.getValue());
//                else exGroupMemberInfos.addAll(vm.exGroupManagement.getValue());
//                adapter.notifyItemRangeInserted(0, vm.exGroupManagement.getValue().size());
//            });
//        });
//    }
//
//}
